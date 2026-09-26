package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.tenant.TenantId;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionReleaseUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENANGA000000000";
	private static final String STAFF_ID = "01STAFFDIMASPRATAMA00000";
	private static final String ORDER_NUMBER = "INV-20260913-4HN2XC";

	@Mock
	PromotionReleaseGateway gateway;
	@InjectMocks
	DefaultPromotionReleaseUseCase useCase;
	@Mock
	PromotionReleasePresenter presenter;
	@Captor
	ArgumentCaptor<PromotionRedemption> redemptionCaptor;
	@Captor
	ArgumentCaptor<PromotionReleaseResponse> responseCaptor;

	@Test
	void givenBlankReferenceId_thenThrowsConstraintViolationException(){
		PromotionReleaseRequest request = new PromotionReleaseRequest(TENANT_ID, "  ", STAFF_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenNoRedemptionForTheReference_thenPresentsAnEmptyReleaseAndTouchesNoUsage(){
		PromotionReleaseRequest request = new PromotionReleaseRequest(TENANT_ID, ORDER_NUMBER, STAFF_ID);
		willReturn(List.of()).given(gateway)
				.findByReferenceId(anyString(), any(TenantId.class));

		useCase.execute(request, presenter);

		then(gateway).should()
				.findByReferenceId(eq(ORDER_NUMBER), eq(new TenantId(TENANT_ID)));
		then(gateway).should(never())
				.releaseUsage(any(PromotionId.class), any(TenantId.class), anyString());
		then(gateway).should(never())
				.save(any(PromotionRedemption.class));
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().referenceId()).isEqualTo(ORDER_NUMBER);
			softly.then(responseCaptor.getValue().released()).isEmpty();
		});
	}

	@Test
	void givenTwoRedemptionsForTheReference_thenReleasesEachUsageAndSoftDeletesEachRow(){
		Instant now = Instant.now();
		PromotionRedemption first = PromotionRedemption.builder()
				.id("01REDEMPTIONGRATISONGKIR")
				.promotionId("01PROMOGRATISONGKIR00000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("GRATISONGKIR"))
				.referenceId(ORDER_NUMBER)
				.subtotal(Money.of(60000L))
				.discountAmount(Money.of(10000L))
				.version(0)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemption second = PromotionRedemption.builder()
				.id("01REDEMPTIONPELANGGANBARU")
				.promotionId("01PROMOPELANGGANBARU0000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("PELANGGANBARU"))
				.referenceId(ORDER_NUMBER)
				.customerId("01CUSTOMERNADIA000000000")
				.subtotal(Money.of(60000L))
				.discountAmount(Money.of(5000L))
				.version(0)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionReleaseRequest request = new PromotionReleaseRequest(TENANT_ID, ORDER_NUMBER,
				"01STAFFRIZKYANANDA000000");
		willReturn(List.of(first, second)).given(gateway)
				.findByReferenceId(anyString(), any(TenantId.class));
		willReturn(true).given(gateway)
				.releaseUsage(any(PromotionId.class), any(TenantId.class), anyString());
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should()
				.releaseUsage(eq(new PromotionId("01PROMOGRATISONGKIR00000")), eq(new TenantId(TENANT_ID)),
						eq("01STAFFRIZKYANANDA000000"));
		then(gateway).should()
				.releaseUsage(eq(new PromotionId("01PROMOPELANGGANBARU0000")), eq(new TenantId(TENANT_ID)),
						eq("01STAFFRIZKYANANDA000000"));
		then(gateway).should(times(2))
				.save(redemptionCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<PromotionRedemption> saved = redemptionCaptor.getAllValues();

		thenSoftly(softly -> {
			softly.then(saved.getFirst().id()).isEqualTo("01REDEMPTIONGRATISONGKIR");
			softly.then(saved.getFirst().deleted()).isTrue();
			softly.then(saved.getFirst().updatedBy()).isEqualTo("01STAFFRIZKYANANDA000000");
			softly.then(saved.getFirst().createdBy()).isEqualTo(STAFF_ID);
			softly.then(saved.getFirst().discountAmount().value()).isEqualByComparingTo(Money.of(10000L).value());
			softly.then(saved.getLast().id()).isEqualTo("01REDEMPTIONPELANGGANBARU");
			softly.then(saved.getLast().deleted()).isTrue();
			softly.then(saved.getLast().customerId()).isEqualTo("01CUSTOMERNADIA000000000");
			softly.then(responseCaptor.getValue().released()).hasSize(2);
		});
	}

}
