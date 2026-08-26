package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionRedemptionUseCaseTest{

	private static final String TENANT_ID = "01TENANTANGGREK000000000";
	private static final String PROMOTION_ID = "01PROMODISKONAKHIRPEKAN0";
	private static final String STAFF_ID = "01STAFFRENIOKTAVIA000000";
	private static final String ORDER_NUMBER = "INV-20260825-7QB3ZK";
	private static final String PROMO_CODE = "AKHIRPEKAN";

	@Mock
	PromotionRedemptionGateway gateway;
	@InjectMocks
	DefaultPromotionRedemptionUseCase useCase;
	@Mock
	PromotionRedemptionPresenter presenter;
	@Captor
	ArgumentCaptor<List<PromotionRedemptionResponse>> responseCaptor;
	@Captor
	ArgumentCaptor<PromotionRedemptionDomain> redemptionCaptor;

	@Test
	void givenUnknownCode_thenRejectsWithNotFound(){
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.empty()).given(gateway)
				.findByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(PromotionDomain.class));

		PromotionRedemptionResponse response = responseCaptor.getValue().getFirst();
		thenSoftly(softly -> {
			softly.then(response.isApplied()).isFalse();
			softly.then(response.rejection()).isEqualTo(PromotionRejection.NOT_FOUND);
		});
	}

	@Test
	void givenExpiredPromotion_thenRejectsWithExpired(){
		Instant now = Instant.now();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new NoteDomain("weekend only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(4)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.minusSeconds(3600L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(PromotionDomain.class));

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.EXPIRED));
	}

	@Test
	void givenARetiredPromotion_thenRejectsWithInactiveRatherThanNotFound(){
		Instant now = Instant.now();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new NoteDomain("weekend only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(7)
				.active(false)
				.deleted(true)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(PromotionDomain.class));

		PromotionRedemptionResponse response = responseCaptor.getValue().getFirst();
		thenSoftly(softly -> {
			softly.then(response.isApplied()).isFalse();
			softly.then(response.rejection()).isEqualTo(PromotionRejection.INACTIVE);
		});
	}

	@Test
	void givenTheUsageSlotIsLostToAConcurrentOrder_thenRejectsWithExhausted(){
		Instant now = Instant.now();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new NoteDomain("weekend only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(true)
				.usageLimit(10)
				.usedCount(9)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));
		willReturn(false).given(gateway)
				.claimUsage(any(PromotionDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.save(any(PromotionRedemptionDomain.class));

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.EXHAUSTED));
	}

	@Test
	void givenMultipleCodesAndANonCombinablePromotion_thenRejectsWithNotCombinable(){
		Instant now = Instant.now();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new NoteDomain("weekend only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(false)
				.usageLimit(50)
				.usedCount(4)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE, "LAINNYA"),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(eq(new PromotionCodeDomain(PROMO_CODE)), any(TenantIdDomain.class));
		willReturn(Optional.empty()).given(gateway)
				.findByCode(eq(new PromotionCodeDomain("LAINNYA")), any(TenantIdDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(PromotionDomain.class));

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.NOT_COMBINABLE));
	}

	@Test
	void givenSubtotalBelowMinSubtotal_thenRejectsWithBelowMinSubtotal(){
		Instant now = Instant.now();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new NoteDomain("weekend only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.minSubtotal(MoneyDomain.of(100000L))
				.combinable(true)
				.usageLimit(50)
				.usedCount(4)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(PromotionDomain.class));

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.BELOW_MIN_SUBTOTAL));
	}

	@Test
	void givenTheSameOrderNumberAndCodeTwice_thenReplaysTheFirstRedemptionWithoutClaimingAgain(){
		Instant now = Instant.now();
		PromotionRedemptionDomain existing = PromotionRedemptionDomain.builder()
				.id("01REDEEMAKHIRPEKAN000000")
				.promotionId(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.referenceId(ORDER_NUMBER)
				.subtotal(MoneyDomain.of(40000L))
				.discountAmount(MoneyDomain.of(8000L))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new NoteDomain("weekend only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(5)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.of(existing)).given(gateway)
				.findByReferenceId(ORDER_NUMBER, PROMO_CODE, new TenantIdDomain(TENANT_ID));
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionIdDomain.class), any(TenantIdDomain.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(PromotionDomain.class));
		then(gateway).should(never())
				.save(any(PromotionRedemptionDomain.class));

		PromotionRedemptionResponse response = responseCaptor.getValue().getFirst();
		thenSoftly(softly -> {
			softly.then(response.isApplied()).isTrue();
			softly.then(response.redemption().discountAmount().value())
					.isEqualByComparingTo(new BigDecimal("8000.00"));
		});
	}

	@Test
	void givenACappedPercentagePromotion_thenGrantsAtMostTheCapAndRecordsTheRedemption(){
		Instant now = Instant.now();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("GAJIAN30"))
				.name("Diskon Gajian")
				.description(new NoteDomain("payday sale"))
				.type(PromotionType.PERCENTAGE)
				.percentage(30.0d)
				.maxDiscountAmount(MoneyDomain.of(15000L))
				.combinable(true)
				.usageLimit(200)
				.usedCount(11)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of("GAJIAN30"),
				new BigDecimal("120000"), ORDER_NUMBER, "01CUSTOMERRENI0000000000", STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCodeDomain.class), any(TenantIdDomain.class));
		willReturn(true).given(gateway)
				.claimUsage(any(PromotionDomain.class));
		willAnswer(invocation -> invocation.<PromotionRedemptionDomain>getArgument(0)).given(gateway)
				.save(any(PromotionRedemptionDomain.class));

		useCase.execute(request, presenter);

		then(gateway).should()
				.save(redemptionCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		PromotionRedemptionDomain redemption = redemptionCaptor.getValue();
		PromotionRedemptionResponse response = responseCaptor.getValue().getFirst();

		thenSoftly(softly -> {
			softly.then(redemption.discountAmount().value()).isEqualByComparingTo(new BigDecimal("15000.00"));
			softly.then(redemption.subtotal().value()).isEqualByComparingTo(new BigDecimal("120000.00"));
			softly.then(redemption.referenceId()).isEqualTo(ORDER_NUMBER);
			softly.then(redemption.customerId()).isEqualTo("01CUSTOMERRENI0000000000");
			softly.then(redemption.createdBy()).isEqualTo(STAFF_ID);
			softly.then(response.isApplied()).isTrue();
			softly.then(response.promotion().usedCount()).isEqualTo(12);
			softly.then(response.promotion().remainingUsage()).isEqualTo(188);
		});
	}

	@Test
	void givenTwoStackedPercentageCodes_thenTheSecondComputesTheDiscountOffTheRemainingAmount(){
		Instant now = Instant.now();
		PromotionDomain first = PromotionDomain.builder()
				.id("01PROMOPERTAMA000000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("PERTAMA"))
				.name("Diskon Pertama")
				.description(new NoteDomain("first code in the stack"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionDomain second = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("KEDUA"))
				.name("Diskon Kedua")
				.description(new NoteDomain("stacked with another code"))
				.type(PromotionType.PERCENTAGE)
				.percentage(10.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of("PERTAMA", "KEDUA"),
				new BigDecimal("100000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(first)).given(gateway)
				.findByCode(eq(new PromotionCodeDomain("PERTAMA")), any(TenantIdDomain.class));
		willReturn(Optional.of(second)).given(gateway)
				.findByCode(eq(new PromotionCodeDomain("KEDUA")), any(TenantIdDomain.class));
		willReturn(true).given(gateway)
				.claimUsage(any(PromotionDomain.class));
		willAnswer(invocation -> invocation.<PromotionRedemptionDomain>getArgument(0)).given(gateway)
				.save(any(PromotionRedemptionDomain.class));

		useCase.execute(request, presenter);

		then(gateway).should(times(2))
				.save(redemptionCaptor.capture());

		List<PromotionRedemptionDomain> redemptions = redemptionCaptor.getAllValues();

		thenSoftly(softly -> {
			softly.then(redemptions.getFirst().discountAmount().value()).isEqualByComparingTo(new BigDecimal("20000.00"));
			softly.then(redemptions.getLast().discountAmount().value()).isEqualByComparingTo(new BigDecimal("8000.00"));
		});
	}

	@Test
	void givenANonCumulativePercentageStackedAfterAnotherCode_thenIgnoresDiscountSoFar(){
		Instant now = Instant.now();
		PromotionDomain first = PromotionDomain.builder()
				.id("01PROMOPERTAMA000000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("PERTAMA"))
				.name("Diskon Pertama")
				.description(new NoteDomain("first code in the stack"))
				.type(PromotionType.PERCENTAGE)
				.percentage(20.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionDomain second = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("KHUSUS"))
				.name("Diskon Khusus")
				.description(new NoteDomain("always off the original subtotal"))
				.type(PromotionType.NON_CUMULATIVE_PERCENTAGE)
				.percentage(10.0d)
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, Set.of("PERTAMA", "KHUSUS"),
				new BigDecimal("100000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantIdDomain.class));
		willReturn(Optional.of(first)).given(gateway)
				.findByCode(eq(new PromotionCodeDomain("PERTAMA")), any(TenantIdDomain.class));
		willReturn(Optional.of(second)).given(gateway)
				.findByCode(eq(new PromotionCodeDomain("KHUSUS")), any(TenantIdDomain.class));
		willReturn(true).given(gateway)
				.claimUsage(any(PromotionDomain.class));
		willAnswer(invocation -> invocation.<PromotionRedemptionDomain>getArgument(0)).given(gateway)
				.save(any(PromotionRedemptionDomain.class));

		useCase.execute(request, presenter);

		then(gateway).should(times(2))
				.save(redemptionCaptor.capture());

		List<PromotionRedemptionDomain> redemptions = redemptionCaptor.getAllValues();

		thenSoftly(softly -> softly.then(redemptions.getLast().discountAmount().value())
				.isEqualByComparingTo(new BigDecimal("10000.00")));
	}

}
