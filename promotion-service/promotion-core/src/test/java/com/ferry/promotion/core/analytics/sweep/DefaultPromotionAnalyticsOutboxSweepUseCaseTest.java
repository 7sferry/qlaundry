package com.ferry.promotion.core.analytics.sweep;

import com.ferry.promotion.core.analytics.PromotionAnalyticsOutboxConstant;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsAggregate;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventStatus;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionAnalyticsOutboxSweepUseCaseTest{

	private static final String TENANT_ID = "01TENANTBUNGATANJUNG00000";
	private static final String STAFF_ID = "01STAFFREZAPAHLEVI0000000";
	private static final Duration GRACE_PERIOD = Duration.ofMinutes(3);
	private static final int SWEEP_BATCH_SIZE = 150;

	@Mock
	PromotionAnalyticsOutboxSweepGateway gateway;
	@Captor
	ArgumentCaptor<Instant> cutoffCaptor;
	@Captor
	ArgumentCaptor<PromotionAnalyticsEvent> eventCaptor;

	@Test
	void givenAnEmptyOutbox_thenLooksBackOnlyPastTheGracePeriodAndRepublishesNothing(){
		DefaultPromotionAnalyticsOutboxSweepUseCase useCase = new DefaultPromotionAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);
		willReturn(List.of()).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		Instant before = Instant.now();

		PromotionAnalyticsOutboxSweepResponse response = useCase.execute();

		Instant after = Instant.now();
		then(gateway).should()
				.findUnpublishedCreatedBefore(cutoffCaptor.capture(), eq(SWEEP_BATCH_SIZE));
		then(gateway).should(never())
				.republish(any(PromotionAnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.isEmpty()).isTrue();
			softly.then(cutoffCaptor.getValue()).isBetween(before.minus(GRACE_PERIOD), after.minus(GRACE_PERIOD));
		});
	}

	@Test
	void givenOneEventFailsAgain_thenRecordsItAndStillRepublishesTheNextOne(){
		Instant createdAt = Instant.now().minusSeconds(900L);
		PromotionAnalyticsEvent stuck = PromotionAnalyticsEvent.builder()
				.id("01EVENTPROMOMACET0000000")
				.aggregate(PromotionAnalyticsAggregate.PROMOTION)
				.type(PromotionAnalyticsEventType.PROMOTION_TOGGLED)
				.tenantId(TENANT_ID)
				.aggregateId("01PROMOHARIBELANJA000000")
				.aggregateVersion(2)
				.payload("{\"active\":false}")
				.status(PromotionAnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.attempts(1)
				.version(1)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(PromotionAnalyticsOutboxConstant.SWEEPER_ACTOR)
				.build();
		PromotionAnalyticsEvent fine = PromotionAnalyticsEvent.builder()
				.id("01EVENTPROMOLANCAR000000")
				.aggregate(PromotionAnalyticsAggregate.PROMOTION)
				.type(PromotionAnalyticsEventType.PROMOTION_CREATED)
				.tenantId(TENANT_ID)
				.aggregateId("01PROMOAKHIRPEKAN0000000")
				.aggregateVersion(0)
				.payload("{\"code\":\"AKHIRPEKAN\"}")
				.status(PromotionAnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.version(0)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(stuck, fine)).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		willThrow(new IllegalStateException("Redis connection refused")).willDoNothing().given(gateway)
				.republish(any(PromotionAnalyticsEvent.class));
		DefaultPromotionAnalyticsOutboxSweepUseCase useCase = new DefaultPromotionAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		PromotionAnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should(times(2))
				.republish(any(PromotionAnalyticsEvent.class));
		then(gateway).should()
				.recordFailure(eventCaptor.capture());

		PromotionAnalyticsEvent failed = eventCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(failed.id()).isEqualTo("01EVENTPROMOMACET0000000");
			softly.then(failed.attempts()).isEqualTo(2);
			softly.then(failed.lastError()).contains("Redis connection refused");
			softly.then(failed.updatedBy()).isEqualTo(PromotionAnalyticsOutboxConstant.SWEEPER_ACTOR);
			softly.then(response.republished()).isEqualTo(1);
			softly.then(response.failed()).isEqualTo(1);
		});
	}

}
