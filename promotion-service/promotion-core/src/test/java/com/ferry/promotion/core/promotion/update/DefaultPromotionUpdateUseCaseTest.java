package com.ferry.promotion.core.promotion.update;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

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
class DefaultPromotionUpdateUseCaseTest{

	private static final String TENANT_ID = "01TENANTTERATAI00000000000";
	private static final String STAFF_ID = "01STAFFYUDHAPERMANA0000000";
	private static final String PROMOTION_ID = "01PROMOAWALBULAN0000000000";

	@Mock
	PromotionUpdateGateway gateway;
	@InjectMocks
	DefaultPromotionUpdateUseCase useCase;
	@Mock
	PromotionUpdatePresenter presenter;
	@Captor
	ArgumentCaptor<PromotionDomain> promotionCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionUpdateRequest request = new PromotionUpdateRequest(PROMOTION_ID, "AWALBULAN", "Diskon Awal Bulan",
				"monthly", PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null, 25, null, null, true);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(PromotionForbiddenActionException.class)
				.hasMessage("Only super staff can manage promotions"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenUnknownPromotion_thenThrowsNotFoundException(){
		Instant startAt = Instant.parse("2026-09-01T00:00:00Z");
		Instant endAt = Instant.parse("2026-09-30T00:00:00Z");
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionUpdateRequest request = new PromotionUpdateRequest(PROMOTION_ID, "AWALBULAN", "Diskon Awal Bulan",
				"monthly", PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null, 25,
				startAt.toEpochMilli(), endAt.toEpochMilli(), true);
		willReturn(Optional.empty()).given(gateway)
				.findById(any(PromotionIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Promotion Not Found"));

		then(gateway).should(never())
				.save(any(PromotionDomain.class));
	}

	@Test
	void givenACodeAlreadyTakenByAnotherPromotion_thenThrowsIllegalArgumentException(){
		Instant now = Instant.now();
		Instant startAt = Instant.parse("2026-09-01T00:00:00Z");
		Instant endAt = Instant.parse("2026-09-30T00:00:00Z");
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("AWALBULAN"))
				.name("Diskon Awal Bulan")
				.description(new NoteDomain("monthly"))
				.type(PromotionType.PERCENTAGE)
				.percentage(new BigDecimal("10"))
				.combinable(true)
				.usageLimit(25)
				.usedCount(3)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionUpdateRequest request = new PromotionUpdateRequest(PROMOTION_ID, "TENGAHBULAN",
				"Diskon Tengah Bulan", "monthly", PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null, 25,
				startAt.toEpochMilli(), endAt.toEpochMilli(), true);
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionIdDomain.class), any(TenantIdDomain.class));
		willReturn(true).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class), any(PromotionIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Promotion code already exists"));

		then(gateway).should(never())
				.save(any(PromotionDomain.class));
	}

	@Test
	void givenAChangeToACappedPercentage_thenKeepsTheUsageAlreadySpent(){
		Instant now = Instant.now();
		Instant startAt = Instant.parse("2026-09-01T00:00:00Z");
		Instant endAt = Instant.parse("2026-09-30T00:00:00Z");
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("AWALBULAN"))
				.name("Diskon Awal Bulan")
				.description(new NoteDomain("monthly"))
				.type(PromotionType.FIXED_AMOUNT)
				.amount(MoneyDomain.of(3000L))
				.combinable(true)
				.usageLimit(25)
				.usedCount(7)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionUpdateRequest request = new PromotionUpdateRequest(PROMOTION_ID, "awalbulan", "Diskon Awal Bulan",
				"monthly capped", PromotionType.PERCENTAGE, new BigDecimal("12.5"), null, new BigDecimal("20000"), null, false, 40,
				startAt.toEpochMilli(), endAt.toEpochMilli(), false);
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionIdDomain.class), any(TenantIdDomain.class));
		willReturn(false).given(gateway)
				.existsByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class), any(PromotionIdDomain.class));
		willAnswer(invocation -> invocation.<PromotionDomain>getArgument(0)).given(gateway)
				.save(any(PromotionDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(promotionCaptor.capture());

		PromotionDomain saved = promotionCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.codeValue()).isEqualTo("AWALBULAN");
			softly.then(saved.type()).isEqualTo(PromotionType.PERCENTAGE);
			softly.then(saved.percentage()).isEqualByComparingTo(new BigDecimal("12.5"));
			softly.then(saved.maxDiscountAmountValue()).isEqualByComparingTo(new BigDecimal("20000.00"));
			softly.then(saved.usageLimit()).isEqualTo(40);
			softly.then(saved.usedCount()).isEqualTo(7);
			softly.then(saved.active()).isFalse();
			softly.then(saved.combinable()).isFalse();
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
		});
	}

}
