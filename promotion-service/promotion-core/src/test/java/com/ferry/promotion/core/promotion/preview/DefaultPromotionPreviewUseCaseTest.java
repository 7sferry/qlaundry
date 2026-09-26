package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.session.SessionType;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionPreviewUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENANGA00000000";
	private static final String STAFF_ID = "01STAFFYULIASTUTI0000000";

	@InjectMocks
	DefaultPromotionPreviewUseCase useCase;
	@Mock
	PromotionPreviewPresenter presenter;
	@Captor
	ArgumentCaptor<PromotionPreviewResponse> responseCaptor;

	private static PromotionAuthPrincipal principal(){
		return PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.sessionType(SessionType.STAFF)
				.role(StaffRole.STAFF)
				.build();
	}

	private static PromotionSnapshot snapshot(String id, String code, PromotionType type, BigDecimal percentage,
	                                          Instant now){
		return new PromotionSnapshot(id, code, "Diskon Kenangan", type, percentage, null, null, null, true,
				null, 3, true, now.minusSeconds(864000L).toEpochMilli(), now.plusSeconds(864000L).toEpochMilli());
	}

	@Test
	void givenEmptyPromotions_thenThrowsConstraintViolationException(){
		PromotionPreviewRequest request = new PromotionPreviewRequest(List.of(), new BigDecimal("50000"));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal(), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(presenter).shouldHaveNoInteractions();
	}

	static Stream<BigDecimal> invalidSubtotal(){
		return Stream.of(null, new BigDecimal("-50000"));
	}

	@ParameterizedTest
	@MethodSource("invalidSubtotal")
	void givenNullOrNegativeSubtotal_thenThrowsConstraintViolationException(BigDecimal subtotal){
		Instant now = Instant.now();
		PromotionSnapshot snapshot = snapshot("01PROMODISKONKENANGA000", "KENANGA10",
				PromotionType.CUMULATIVE_PERCENTAGE, new BigDecimal("10"), now);
		PromotionPreviewRequest request = new PromotionPreviewRequest(List.of(snapshot), subtotal);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal(), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenExpiredPromotion_thenPreviewsExpiredWithoutTouchingAnyGateway(){
		Instant now = Instant.now();
		PromotionSnapshot expired = new PromotionSnapshot("01PROMODISKONKENANGA000", "KENANGA10",
				"Diskon Kenangan", PromotionType.CUMULATIVE_PERCENTAGE, new BigDecimal("10"), null, null, null,
				true, null, 2, true, now.minusSeconds(864000L).toEpochMilli(), now.minusSeconds(3600L).toEpochMilli());
		PromotionPreviewRequest request = new PromotionPreviewRequest(List.of(expired), new BigDecimal("40000"));

		useCase.execute(request, principal(), presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		thenSoftly(softly -> softly.then(responseCaptor.getValue().previews().getFirst().rejection())
				.isEqualTo(PromotionRejection.EXPIRED));
	}

	@Test
	void givenTwoStackedCumulativePercentageSnapshots_thenChainsTheDiscountBasisInSubmittedOrder(){
		Instant now = Instant.now();
		PromotionSnapshot first = snapshot("01PROMODISKONKENANGA000", "KENANGA10",
				PromotionType.CUMULATIVE_PERCENTAGE, new BigDecimal("10"), now);
		PromotionSnapshot second = snapshot("01PROMODISKONKENANGA001", "KENANGA20",
				PromotionType.CUMULATIVE_PERCENTAGE, new BigDecimal("20"), now);
		PromotionPreviewRequest request = new PromotionPreviewRequest(List.of(first, second),
				new BigDecimal("100000"));

		useCase.execute(request, principal(), presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		List<PromotionPreviewResult> previews = responseCaptor.getValue().previews();
		thenSoftly(softly -> {
			softly.then(previews.get(0).isApplied()).isTrue();
			softly.then(previews.get(0).discountAmount().value()).isEqualByComparingTo("10000.00");
			softly.then(previews.get(1).isApplied()).isTrue();
			softly.then(previews.get(1).discountAmount().value()).isEqualByComparingTo("18000.00");
		});
	}

	@Test
	void givenNonCombinablePromotionStackedWithAnother_thenRejectsWithNotCombinable(){
		Instant now = Instant.now();
		PromotionSnapshot exclusive = new PromotionSnapshot("01PROMODISKONKENANGA002", "SOLO50",
				"Diskon Solo", PromotionType.FIXED_AMOUNT, null, new BigDecimal("5000"), null, null, false,
				null, 0, true, now.minusSeconds(864000L).toEpochMilli(), now.plusSeconds(864000L).toEpochMilli());
		PromotionSnapshot other = snapshot("01PROMODISKONKENANGA003", "KENANGA10",
				PromotionType.CUMULATIVE_PERCENTAGE, new BigDecimal("10"), now);
		PromotionPreviewRequest request = new PromotionPreviewRequest(List.of(exclusive, other),
				new BigDecimal("40000"));

		useCase.execute(request, principal(), presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		thenSoftly(softly -> softly.then(responseCaptor.getValue().previews().getFirst().rejection())
				.isEqualTo(PromotionRejection.NOT_COMBINABLE));
	}

}
