package com.ferry.promotion.core.analytics.backfill;

import com.ferry.promotion.core.analytics.PromotionAnalyticsEventConfig;
import com.ferry.promotion.core.analytics.PromotionAnalyticsOutboxConstant;
import com.ferry.promotion.core.analytics.PromotionAnalyticsPublisher;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventType;
import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.PromotionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionAnalyticsBackfillUseCaseTest{

	private static final String TENANT_ID = "01TENANTWIJAYAKUSUMA00000";
	private static final String STAFF_ID = "01STAFFNOVIANTI0000000000";

	@Mock
	PromotionAnalyticsBackfillGateway gateway;
	@Mock
	PromotionAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<PromotionAnalyticsEventConfig> configCaptor;

	@Test
	void givenThreePromotionsAndABatchOfTwo_thenPagesByIdAndPublishesEachAsBackfilled(){
		Instant now = Instant.now();
		Promotion ramadan = Promotion.builder()
				.id("01PROMORAMADAN00000000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("RAMADAN"))
				.name("Promo Ramadan")
				.type(PromotionType.CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("15"))
				.combinable(true)
				.startAt(now.minusSeconds(86400L))
				.endAt(now.plusSeconds(86400L))
				.active(true)
				.version(4)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Promotion kemerdekaan = Promotion.builder()
				.id("01PROMOKEMERDEKAAN0000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("MERDEKA"))
				.name("Promo Kemerdekaan")
				.type(PromotionType.FIXED_AMOUNT)
				.amount(Money.of(17000L))
				.startAt(now.minusSeconds(86400L))
				.endAt(now.plusSeconds(86400L))
				.active(false)
				.version(1)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Promotion tahunBaru = Promotion.builder()
				.id("01PROMOTAHUNBARU000000000")
				.tenantId(TENANT_ID)
				.code(new PromotionCode("TAHUNBARU"))
				.name("Promo Tahun Baru")
				.type(PromotionType.NON_CUMULATIVE_PERCENTAGE)
				.percentage(new BigDecimal("20"))
				.startAt(now.plusSeconds(86400L))
				.endAt(now.plusSeconds(172800L))
				.active(true)
				.version(0)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(ramadan, kemerdekaan)).given(gateway)
				.findPromotionsAfter(isNull(), isNull(), anyInt());
		willReturn(List.of(tahunBaru)).given(gateway)
				.findPromotionsAfter(isNull(), anyString(), anyInt());
		DefaultPromotionAnalyticsBackfillUseCase useCase = new DefaultPromotionAnalyticsBackfillUseCase(gateway, publisher, 2);

		PromotionAnalyticsBackfillResponse response = useCase.execute(new PromotionAnalyticsBackfillRequest("  "));

		then(gateway).should()
				.findPromotionsAfter(isNull(), eq("01PROMOKEMERDEKAAN0000000"), eq(2));
		then(publisher).should(times(3))
				.save(configCaptor.capture());

		List<PromotionAnalyticsEventConfig> configs = configCaptor.getAllValues();

		thenSoftly(softly -> {
			softly.then(response.promotions()).isEqualTo(3);
			softly.then(configs).extracting(PromotionAnalyticsEventConfig::type)
					.containsOnly(PromotionAnalyticsEventType.PROMOTION_BACKFILLED);
			softly.then(configs).extracting(PromotionAnalyticsEventConfig::aggregateId)
					.containsExactly("01PROMORAMADAN00000000000", "01PROMOKEMERDEKAAN0000000",
							"01PROMOTAHUNBARU000000000");
			softly.then(configs).extracting(PromotionAnalyticsEventConfig::aggregateVersion).containsExactly(4, 1, 0);
			softly.then(configs).extracting(PromotionAnalyticsEventConfig::actor)
					.containsOnly(PromotionAnalyticsOutboxConstant.BACKFILL_ACTOR);
		});
	}

	@Test
	void givenOneTenantAndNoPromotions_thenPublishesNothing(){
		willReturn(List.of()).given(gateway)
				.findPromotionsAfter(anyString(), isNull(), anyInt());
		DefaultPromotionAnalyticsBackfillUseCase useCase = new DefaultPromotionAnalyticsBackfillUseCase(gateway, publisher, 500);

		PromotionAnalyticsBackfillResponse response = useCase.execute(new PromotionAnalyticsBackfillRequest(' ' + TENANT_ID + ' '));

		then(gateway).should()
				.findPromotionsAfter(eq(TENANT_ID), isNull(), eq(500));
		then(publisher).shouldHaveNoInteractions();

		thenSoftly(softly -> softly.then(response.promotions()).isZero());
	}

}
