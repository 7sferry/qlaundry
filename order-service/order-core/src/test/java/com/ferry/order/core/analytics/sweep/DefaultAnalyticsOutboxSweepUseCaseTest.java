package com.ferry.order.core.analytics.sweep;

import com.ferry.order.core.analytics.AnalyticsOutboxConstant;
import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEvent;
import com.ferry.order.domain.analytics.AnalyticsEventStatus;
import com.ferry.order.domain.analytics.AnalyticsEventType;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultAnalyticsOutboxSweepUseCaseTest{

	private static final String TENANT_ID = "01TENANTTERATAIPUTIH00000";
	private static final String STAFF_ID = "01STAFFGILANGRAMADHAN0000";
	private static final Duration GRACE_PERIOD = Duration.ofMinutes(2);
	private static final int SWEEP_BATCH_SIZE = 200;

	@Mock
	AnalyticsOutboxSweepGateway gateway;
	@Captor
	ArgumentCaptor<Instant> cutoffCaptor;
	@Captor
	ArgumentCaptor<AnalyticsEvent> eventCaptor;

	@Test
	void givenNothingStuckInTheOutbox_thenReturnsAnEmptySweepLookingBackOnlyPastTheGracePeriod(){
		DefaultAnalyticsOutboxSweepUseCase useCase = new DefaultAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);
		willReturn(List.of()).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		Instant before = Instant.now();

		AnalyticsOutboxSweepResponse response = useCase.execute();

		Instant after = Instant.now();
		then(gateway).should()
				.findUnpublishedCreatedBefore(cutoffCaptor.capture(), eq(SWEEP_BATCH_SIZE));
		then(gateway).should(never())
				.republish(any(AnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.isEmpty()).isTrue();
			softly.then(cutoffCaptor.getValue())
					.isBetween(before.minus(GRACE_PERIOD), after.minus(GRACE_PERIOD));
		});
	}

	@Test
	void givenAnEventLeftCreatedByARedisOutage_thenRepublishesIt(){
		Instant createdAt = Instant.now().minusSeconds(600L);
		AnalyticsEvent event = AnalyticsEvent.builder()
				.id("01EVENTREDISMATIPAGI00000")
				.aggregate(AnalyticsAggregate.ORDER)
				.type(AnalyticsEventType.ORDER_CREATED)
				.tenantId(TENANT_ID)
				.aggregateId("01ORDERSELIMUTTEBAL000000")
				.aggregateVersion(0)
				.payload("{\"status\":\"PENDING\"}")
				.status(AnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.version(0)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(event)).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		DefaultAnalyticsOutboxSweepUseCase useCase = new DefaultAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		AnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should()
				.republish(eq(event));
		then(gateway).should(never())
				.recordFailure(any(AnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.republished()).isEqualTo(1);
			softly.then(response.failed()).isZero();
		});
	}

	@Test
	void givenRedisStillDown_thenRecordsTheFailureAndKeepsSweepingTheRestOfTheBatch(){
		Instant createdAt = Instant.now().minusSeconds(1800L);
		AnalyticsEvent first = AnalyticsEvent.builder()
				.id("01EVENTGAGALLAGI000000000")
				.aggregate(AnalyticsAggregate.LAUNDRY_SERVICE)
				.type(AnalyticsEventType.LAUNDRY_SERVICE_UPDATED)
				.tenantId(TENANT_ID)
				.aggregateId("01SERVICECUCIBONEKA000000")
				.aggregateVersion(3)
				.payload("{\"name\":\"Cuci Boneka\"}")
				.status(AnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.attempts(2)
				.version(2)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(AnalyticsOutboxConstant.SWEEPER_ACTOR)
				.build();
		AnalyticsEvent second = AnalyticsEvent.builder()
				.id("01EVENTBERHASILKEDUA00000")
				.aggregate(AnalyticsAggregate.ORDER)
				.type(AnalyticsEventType.ORDER_PAID)
				.tenantId("01TENANTLAVENDERWANGI0000")
				.aggregateId("01ORDERKEBAYAPENGANTIN000")
				.aggregateVersion(4)
				.payload("{\"paymentStatus\":\"PAID\"}")
				.status(AnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.version(0)
				.createdAt(createdAt)
				.createdBy("01STAFFMELATISUKMA0000000")
				.updatedAt(createdAt)
				.updatedBy("01STAFFMELATISUKMA0000000")
				.build();
		willReturn(List.of(first, second)).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		willThrow(new IllegalStateException("Unable to connect to localhost:6379")).willDoNothing().given(gateway)
				.republish(any(AnalyticsEvent.class));
		DefaultAnalyticsOutboxSweepUseCase useCase = new DefaultAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		AnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should(times(2))
				.republish(any(AnalyticsEvent.class));
		then(gateway).should()
				.recordFailure(eventCaptor.capture());

		AnalyticsEvent failed = eventCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(failed.id()).isEqualTo("01EVENTGAGALLAGI000000000");
			softly.then(failed.attempts()).isEqualTo(3);
			softly.then(failed.status()).isEqualTo(AnalyticsEventStatus.CREATED);
			softly.then(failed.lastError())
					.contains(IllegalStateException.class.getName())
					.contains("Unable to connect to localhost:6379");
			softly.then(failed.updatedBy()).isEqualTo(AnalyticsOutboxConstant.SWEEPER_ACTOR);
			softly.then(response.republished()).isEqualTo(1);
			softly.then(response.failed()).isEqualTo(1);
		});
	}

}
