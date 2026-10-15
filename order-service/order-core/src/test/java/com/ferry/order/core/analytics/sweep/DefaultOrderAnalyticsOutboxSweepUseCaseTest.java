package com.ferry.order.core.analytics.sweep;

import com.ferry.order.core.analytics.OrderAnalyticsOutboxConstant;
import com.ferry.order.domain.analytics.OrderAnalyticsAggregate;
import com.ferry.order.domain.analytics.OrderAnalyticsEvent;
import com.ferry.order.domain.analytics.OrderAnalyticsEventStatus;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;
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
class DefaultOrderAnalyticsOutboxSweepUseCaseTest{

	private static final String TENANT_ID = "01TENANTTERATAIPUTIH00000";
	private static final String STAFF_ID = "01STAFFGILANGRAMADHAN0000";
	private static final Duration GRACE_PERIOD = Duration.ofMinutes(2);
	private static final int SWEEP_BATCH_SIZE = 200;

	@Mock
	OrderAnalyticsOutboxSweepGateway gateway;
	@Captor
	ArgumentCaptor<Instant> cutoffCaptor;
	@Captor
	ArgumentCaptor<OrderAnalyticsEvent> eventCaptor;

	@Test
	void givenNothingStuckInTheOutbox_thenReturnsAnEmptySweepLookingBackOnlyPastTheGracePeriod(){
		DefaultOrderAnalyticsOutboxSweepUseCase useCase = new DefaultOrderAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);
		willReturn(List.of()).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		Instant before = Instant.now();

		OrderAnalyticsOutboxSweepResponse response = useCase.execute();

		Instant after = Instant.now();
		then(gateway).should()
				.findUnpublishedCreatedBefore(cutoffCaptor.capture(), eq(SWEEP_BATCH_SIZE));
		then(gateway).should(never())
				.republish(any(OrderAnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.isEmpty()).isTrue();
			softly.then(cutoffCaptor.getValue())
					.isBetween(before.minus(GRACE_PERIOD), after.minus(GRACE_PERIOD));
		});
	}

	@Test
	void givenAnEventLeftCreatedByARedisOutage_thenRepublishesIt(){
		Instant createdAt = Instant.now().minusSeconds(600L);
		OrderAnalyticsEvent event = OrderAnalyticsEvent.builder()
				.id("01EVENTREDISMATIPAGI00000")
				.aggregate(OrderAnalyticsAggregate.ORDER)
				.type(OrderAnalyticsEventType.ORDER_CREATED)
				.tenantId(TENANT_ID)
				.aggregateId("01ORDERSELIMUTTEBAL000000")
				.aggregateVersion(0)
				.payload("{\"status\":\"PENDING\"}")
				.status(OrderAnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.version(0)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(event)).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		DefaultOrderAnalyticsOutboxSweepUseCase useCase = new DefaultOrderAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		OrderAnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should()
				.republish(eq(event));
		then(gateway).should(never())
				.recordFailure(any(OrderAnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.republished()).isEqualTo(1);
			softly.then(response.failed()).isZero();
		});
	}

	@Test
	void givenRedisStillDown_thenRecordsTheFailureAndKeepsSweepingTheRestOfTheBatch(){
		Instant createdAt = Instant.now().minusSeconds(1800L);
		OrderAnalyticsEvent first = OrderAnalyticsEvent.builder()
				.id("01EVENTGAGALLAGI000000000")
				.aggregate(OrderAnalyticsAggregate.LAUNDRY_SERVICE)
				.type(OrderAnalyticsEventType.LAUNDRY_SERVICE_UPDATED)
				.tenantId(TENANT_ID)
				.aggregateId("01SERVICECUCIBONEKA000000")
				.aggregateVersion(3)
				.payload("{\"name\":\"Cuci Boneka\"}")
				.status(OrderAnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.attempts(2)
				.version(2)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(OrderAnalyticsOutboxConstant.SWEEPER_ACTOR)
				.build();
		OrderAnalyticsEvent second = OrderAnalyticsEvent.builder()
				.id("01EVENTBERHASILKEDUA00000")
				.aggregate(OrderAnalyticsAggregate.ORDER)
				.type(OrderAnalyticsEventType.ORDER_PAID)
				.tenantId("01TENANTLAVENDERWANGI0000")
				.aggregateId("01ORDERKEBAYAPENGANTIN000")
				.aggregateVersion(4)
				.payload("{\"paymentStatus\":\"PAID\"}")
				.status(OrderAnalyticsEventStatus.CREATED)
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
				.republish(any(OrderAnalyticsEvent.class));
		DefaultOrderAnalyticsOutboxSweepUseCase useCase = new DefaultOrderAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		OrderAnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should(times(2))
				.republish(any(OrderAnalyticsEvent.class));
		then(gateway).should()
				.recordFailure(eventCaptor.capture());

		OrderAnalyticsEvent failed = eventCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(failed.id()).isEqualTo("01EVENTGAGALLAGI000000000");
			softly.then(failed.attempts()).isEqualTo(3);
			softly.then(failed.status()).isEqualTo(OrderAnalyticsEventStatus.CREATED);
			softly.then(failed.lastError())
					.contains(IllegalStateException.class.getName())
					.contains("Unable to connect to localhost:6379");
			softly.then(failed.updatedBy()).isEqualTo(OrderAnalyticsOutboxConstant.SWEEPER_ACTOR);
			softly.then(response.republished()).isEqualTo(1);
			softly.then(response.failed()).isEqualTo(1);
		});
	}

}
