package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.common.Note;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.tenant.TenantId;
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
import java.util.Optional;
import java.util.stream.Stream;

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
	ArgumentCaptor<PromotionRedemption> redemptionCaptor;

	static Stream<BigDecimal> invalidSubtotal(){
		return Stream.of(null, new BigDecimal("-40000"));
	}

	@ParameterizedTest
	@MethodSource("invalidSubtotal")
	void givenNullOrNegativeSubtotal_thenThrowsConstraintViolationException(BigDecimal subtotal){
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				subtotal, ORDER_NUMBER, null, STAFF_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUnknownCode_thenRejectsWithNotFound(){
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.empty()).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(Promotion.class));
		then(gateway).should()
				.rollback();

		PromotionRedemptionResponse response = responseCaptor.getValue().getFirst();
		thenSoftly(softly -> {
			softly.then(response.isApplied()).isFalse();
			softly.then(response.rejection()).isEqualTo(PromotionRejection.NOT_FOUND);
		});
	}

	@Test
	void givenExpiredPromotion_thenRejectsWithExpired(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new Note("weekend only"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
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
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(Promotion.class));
		then(gateway).should()
				.rollback();

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.EXPIRED));
	}

	@Test
	void givenARetiredPromotion_thenRejectsWithInactiveRatherThanNotFound(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new Note("weekend only"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(7)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(false)
				.deleted(true)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(Promotion.class));
		then(gateway).should()
				.rollback();

		PromotionRedemptionResponse response = responseCaptor.getValue().getFirst();
		thenSoftly(softly -> {
			softly.then(response.isApplied()).isFalse();
			softly.then(response.rejection()).isEqualTo(PromotionRejection.INACTIVE);
		});
	}

	@Test
	void givenTheUsageSlotIsLostToAConcurrentOrder_thenRejectsWithExhausted(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new Note("weekend only"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.combinable(true)
				.usageLimit(10)
				.usedCount(9)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));
		willReturn(false).given(gateway)
				.claimUsage(any(Promotion.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.save(any(PromotionRedemption.class));
		then(gateway).should()
				.rollback();

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.EXHAUSTED));
	}

	@Test
	void givenMultipleCodesAndANonCombinablePromotion_thenRejectsWithNotCombinable(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new Note("weekend only"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.combinable(false)
				.usageLimit(50)
				.usedCount(4)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE, "LAINNYA"),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(eq(new PromotionCode(PROMO_CODE)), any(TenantId.class));
		willReturn(Optional.empty()).given(gateway)
				.findByCode(eq(new PromotionCode("LAINNYA")), any(TenantId.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(Promotion.class));
		then(gateway).should()
				.rollback();

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.NOT_COMBINABLE));
	}

	@Test
	void givenSubtotalBelowMinSubtotal_thenRejectsWithBelowMinSubtotal(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new Note("weekend only"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.minSubtotal(Money.of(100000L))
				.combinable(true)
				.usageLimit(50)
				.usedCount(4)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(Promotion.class));
		then(gateway).should()
				.rollback();

		thenSoftly(softly -> softly.then(responseCaptor.getValue().getFirst().rejection())
				.isEqualTo(PromotionRejection.BELOW_MIN_SUBTOTAL));
	}

	@Test
	void givenTheSameOrderNumberAndCodeTwice_thenReplaysTheFirstRedemptionWithoutClaimingAgain(){
		Instant now = Instant.now();
		PromotionRedemption existing = PromotionRedemption.builder()
				.id("01REDEEMAKHIRPEKAN000000")
				.promotionId(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.referenceId(ORDER_NUMBER)
				.subtotal(Money.of(40000L))
				.discountAmount(Money.of(8000L))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode(PROMO_CODE))
				.name("Diskon Akhir Pekan")
				.description(new Note("weekend only"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(5)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of(PROMO_CODE),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.of(existing)).given(gateway)
				.findByReferenceId(ORDER_NUMBER, PROMO_CODE, new TenantId(TENANT_ID));
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionId.class), any(TenantId.class));

		useCase.execute(request, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.claimUsage(any(Promotion.class));
		then(gateway).should(never())
				.save(any(PromotionRedemption.class));
		then(gateway).should(never())
				.rollback();

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
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("GAJIAN30"))
				.name("Diskon Gajian")
				.description(new Note("payday sale"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("30"))
				.maxDiscountAmount(Money.of(15000L))
				.combinable(true)
				.usageLimit(200)
				.usedCount(11)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of("GAJIAN30"),
				new BigDecimal("120000"), ORDER_NUMBER, "01CUSTOMERRENI0000000000", STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));
		willReturn(true).given(gateway)
				.claimUsage(any(Promotion.class));
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should()
				.save(redemptionCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.rollback();

		PromotionRedemption redemption = redemptionCaptor.getValue();
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
		Promotion first = Promotion.builder()
				.id("01PROMOPERTAMA000000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("PERTAMA"))
				.name("Diskon Pertama")
				.description(new Note("first code in the stack"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Promotion second = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("KEDUA"))
				.name("Diskon Kedua")
				.description(new Note("stacked with another code"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("10"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of("PERTAMA", "KEDUA"),
				new BigDecimal("100000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(first)).given(gateway)
				.findByCode(eq(new PromotionCode("PERTAMA")), any(TenantId.class));
		willReturn(Optional.of(second)).given(gateway)
				.findByCode(eq(new PromotionCode("KEDUA")), any(TenantId.class));
		willReturn(true).given(gateway)
				.claimUsage(any(Promotion.class));
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should(times(2))
				.save(redemptionCaptor.capture());
		then(gateway).should(never())
				.rollback();

		List<PromotionRedemption> redemptions = redemptionCaptor.getAllValues();

		thenSoftly(softly -> {
			softly.then(redemptions.getFirst().discountAmount().value()).isEqualByComparingTo(new BigDecimal("20000.00"));
			softly.then(redemptions.getLast().discountAmount().value()).isEqualByComparingTo(new BigDecimal("8000.00"));
		});
	}

	@Test
	void givenANonCumulativePercentageStackedAfterAnotherCode_thenIgnoresDiscountSoFar(){
		Instant now = Instant.now();
		Promotion first = Promotion.builder()
				.id("01PROMOPERTAMA000000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("PERTAMA"))
				.name("Diskon Pertama")
				.description(new Note("first code in the stack"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Promotion second = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("KHUSUS"))
				.name("Diskon Khusus")
				.description(new Note("always off the original subtotal"))
				.type(PromotionType.NON_CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("10"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of("PERTAMA", "KHUSUS"),
				new BigDecimal("100000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(first)).given(gateway)
				.findByCode(eq(new PromotionCode("PERTAMA")), any(TenantId.class));
		willReturn(Optional.of(second)).given(gateway)
				.findByCode(eq(new PromotionCode("KHUSUS")), any(TenantId.class));
		willReturn(true).given(gateway)
				.claimUsage(any(Promotion.class));
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should(times(2))
				.save(redemptionCaptor.capture());
		then(gateway).should(never())
				.rollback();

		List<PromotionRedemption> redemptions = redemptionCaptor.getAllValues();

		thenSoftly(softly -> softly.then(redemptions.getLast().discountAmount().value())
				.isEqualByComparingTo(new BigDecimal("10000.00")));
	}

	@Test
	void givenAFixedAmountPromotion_thenGrantsTheExactAmount(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("POTONGAN"))
				.name("Diskon Potongan")
				.description(new Note("flat cut"))
				.type(PromotionType.FIXED_AMOUNT)
				.amount(Money.of(8000L))
				.combinable(true)
				.usageLimit(50)
				.usedCount(2)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of("POTONGAN"),
				new BigDecimal("40000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));
		willReturn(true).given(gateway)
				.claimUsage(any(Promotion.class));
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should()
				.save(redemptionCaptor.capture());
		then(gateway).should(never())
				.rollback();

		thenSoftly(softly -> softly.then(redemptionCaptor.getValue().discountAmount().value())
				.isEqualByComparingTo(new BigDecimal("8000.00")));
	}

	@Test
	void givenAFixedAmountPromotionAboveItsCap_thenGrantsAtMostTheCap(){
		Instant now = Instant.now();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("POTONGANBESAR"))
				.name("Diskon Potongan Besar")
				.description(new Note("flat cut, capped"))
				.type(PromotionType.FIXED_AMOUNT)
				.amount(Money.of(20000L))
				.maxDiscountAmount(Money.of(15000L))
				.combinable(true)
				.usageLimit(50)
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
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of("POTONGANBESAR"),
				new BigDecimal("50000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(promotion)).given(gateway)
				.findByCode(any(PromotionCode.class), any(TenantId.class));
		willReturn(true).given(gateway)
				.claimUsage(any(Promotion.class));
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should()
				.save(redemptionCaptor.capture());

		thenSoftly(softly -> softly.then(redemptionCaptor.getValue().discountAmount().value())
				.isEqualByComparingTo(new BigDecimal("15000.00")));
	}

	@Test
	void givenAFirstStackedCodeIsCappedByItsOwnMax_thenTheSecondCodeComputesOffTheCappedRemainder(){
		Instant now = Instant.now();
		Promotion first = Promotion.builder()
				.id("01PROMOPOTONGANBESAR0000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("SETENGAH"))
				.name("Diskon Setengah")
				.description(new Note("half off, capped"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("50"))
				.maxDiscountAmount(Money.of(10000L))
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Promotion second = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("SEPULUH"))
				.name("Diskon Sepuluh")
				.description(new Note("stacked after the capped code"))
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("10"))
				.combinable(true)
				.usageLimit(50)
				.usedCount(0)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionRedemptionRequest request = new PromotionRedemptionRequest(TENANT_ID, List.of("SETENGAH", "SEPULUH"),
				new BigDecimal("100000"), ORDER_NUMBER, null, STAFF_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByReferenceId(anyString(), anyString(), any(TenantId.class));
		willReturn(Optional.of(first)).given(gateway)
				.findByCode(eq(new PromotionCode("SETENGAH")), any(TenantId.class));
		willReturn(Optional.of(second)).given(gateway)
				.findByCode(eq(new PromotionCode("SEPULUH")), any(TenantId.class));
		willReturn(true).given(gateway)
				.claimUsage(any(Promotion.class));
		willAnswer(invocation -> invocation.<PromotionRedemption>getArgument(0)).given(gateway)
				.save(any(PromotionRedemption.class));

		useCase.execute(request, presenter);

		then(gateway).should(times(2))
				.save(redemptionCaptor.capture());
		then(gateway).should(never())
				.rollback();

		List<PromotionRedemption> redemptions = redemptionCaptor.getAllValues();

		thenSoftly(softly -> {
			softly.then(redemptions.getFirst().discountAmount().value()).isEqualByComparingTo(new BigDecimal("10000.00"));
			softly.then(redemptions.getLast().discountAmount().value()).isEqualByComparingTo(new BigDecimal("9000.00"));
		});
	}

}
