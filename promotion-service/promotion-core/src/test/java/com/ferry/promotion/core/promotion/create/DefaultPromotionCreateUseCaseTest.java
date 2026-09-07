package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionCreateUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENANGA0000000000";
	private static final String STAFF_ID = "01STAFFBAGASPRATAMA000000";
	private static final String PROMO_CODE = "LEBARAN25";

	@Mock
	PromotionCreateGateway gateway;
	@InjectMocks
	DefaultPromotionCreateUseCase useCase;
	@Mock
	PromotionCreatePresenter presenter;
	@Captor
	ArgumentCaptor<PromotionDomain> promotionCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest(PROMO_CODE, "Diskon Lebaran", "seasonal",
				PromotionType.PERCENTAGE, new BigDecimal("25"), null, null, null, null, 100, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(PromotionForbiddenActionException.class)
				.hasMessage("Only super staff can manage promotions"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankCode_thenThrowsConstraintViolationException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest("   ", "Diskon Lebaran", "seasonal",
				PromotionType.PERCENTAGE, new BigDecimal("25"), null, null, null, null, 100, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenDuplicateCode_thenThrowsIllegalArgumentException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest(PROMO_CODE, "Diskon Lebaran", "seasonal",
				PromotionType.PERCENTAGE, new BigDecimal("25"), null, null, null, null, 100, null, null);
		willReturn(true).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Promotion code already exists"));

		then(gateway).should(never())
				.save(any(PromotionDomain.class));
	}

	@Test
	void givenPercentageOverOneHundred_thenThrowsIllegalArgumentException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest(PROMO_CODE, "Diskon Lebaran", "seasonal",
				PromotionType.PERCENTAGE, new BigDecimal("120"), null, null, null, null, 100, null, null);
		willReturn(false).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Percentage must be greater than zero and at most 100"));

		then(gateway).should(never())
				.save(any(PromotionDomain.class));
	}

	@Test
	void givenNonPositiveMaxDiscountAmount_thenThrowsIllegalArgumentException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest("KILAT15", "Diskon Kilat", "flash sale",
				PromotionType.PERCENTAGE, new BigDecimal("15"), null, BigDecimal.ZERO, null, null, 50, null, null);
		willReturn(false).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Maximum discount amount must be greater than zero"));

		then(gateway).should(never())
				.save(any(PromotionDomain.class));
	}

	@Test
	void givenNonPositiveMinSubtotal_thenThrowsIllegalArgumentException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest("BELANJA", "Diskon Belanja", "min spend",
				PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, new BigDecimal("-1"), null, 50, null, null);
		willReturn(false).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Amount must not be negative"));

		then(gateway).should(never())
				.save(any(PromotionDomain.class));
	}

	@Test
	void givenValidFixedAmountRequest_thenSavesAnActivePromotionWithNoUsageYet(){
		Instant startAt = Instant.parse("2026-09-01T00:00:00Z");
		Instant endAt = Instant.parse("2026-09-30T00:00:00Z");
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest("potongan5rb", "Potongan 5 Ribu", "flat cut",
				PromotionType.FIXED_AMOUNT, null, new BigDecimal("5000"), null, null, null, 30,
				startAt.toEpochMilli(), endAt.toEpochMilli());
		willReturn(false).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<PromotionDomain>getArgument(0)).given(gateway)
				.save(any(PromotionDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(promotionCaptor.capture());
		then(presenter).should()
				.present(any(PromotionCreateResponse.class));

		PromotionDomain saved = promotionCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.codeValue()).isEqualTo("POTONGAN5RB");
			softly.then(saved.tenantId()).isEqualTo(TENANT_ID);
			softly.then(saved.type()).isEqualTo(PromotionType.FIXED_AMOUNT);
			softly.then(saved.amountValue()).isEqualByComparingTo(new BigDecimal("5000.00"));
			softly.then(saved.usageLimit()).isEqualTo(30);
			softly.then(saved.usedCount()).isZero();
			softly.then(saved.remainingUsage()).isEqualTo(30);
			softly.then(saved.startAt()).isEqualTo(startAt);
			softly.then(saved.endAt()).isEqualTo(endAt);
			softly.then(saved.active()).isTrue();
			softly.then(saved.deleted()).isFalse();
			softly.then(saved.createdBy()).isEqualTo(STAFF_ID);
			softly.then(saved.combinable()).isTrue();
		});
	}

	@Test
	void givenNonCumulativePercentageWithMinSubtotalAndNotCombinable_thenSavesThemAsGiven(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionCreateRequest request = new PromotionCreateRequest("EKSKLUSIF", "Diskon Eksklusif", "vip only",
				PromotionType.NON_CUMULATIVE_PERCENTAGE, new BigDecimal("30"), null, new BigDecimal("75000"),
				new BigDecimal("200000"), false, null, null, null);
		willReturn(false).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<PromotionDomain>getArgument(0)).given(gateway)
				.save(any(PromotionDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(promotionCaptor.capture());

		PromotionDomain saved = promotionCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.type()).isEqualTo(PromotionType.NON_CUMULATIVE_PERCENTAGE);
			softly.then(saved.maxDiscountAmountValue()).isEqualByComparingTo(new BigDecimal("75000.00"));
			softly.then(saved.minSubtotalValue()).isEqualByComparingTo(new BigDecimal("200000.00"));
			softly.then(saved.combinable()).isFalse();
		});
	}

}
