package com.ferry.user.core.analytics.sweep;

import com.ferry.user.core.analytics.UserAnalyticsOutboxConstant;
import com.ferry.user.domain.analytics.UserAnalyticsAggregate;
import com.ferry.user.domain.analytics.UserAnalyticsEvent;
import com.ferry.user.domain.analytics.UserAnalyticsEventStatus;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
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

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultUserAnalyticsOutboxSweepUseCaseTest{

	private static final String TENANT_ID = "01TENANTPUSPAWANGI0000000";
	private static final String STAFF_ID = "01STAFFANDIKURNIAWAN00000";
	private static final Duration GRACE_PERIOD = Duration.ofMinutes(2);
	private static final int SWEEP_BATCH_SIZE = 200;

	@Mock
	UserAnalyticsOutboxSweepGateway gateway;
	@Captor
	ArgumentCaptor<Instant> cutoffCaptor;
	@Captor
	ArgumentCaptor<UserAnalyticsEvent> eventCaptor;

	@Test
	void givenNothingStuck_thenAsksOnlyForRowsOlderThanTheGracePeriod(){
		DefaultUserAnalyticsOutboxSweepUseCase useCase = new DefaultUserAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);
		willReturn(List.of()).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		Instant before = Instant.now();

		UserAnalyticsOutboxSweepResponse response = useCase.execute();

		Instant after = Instant.now();
		then(gateway).should()
				.findUnpublishedCreatedBefore(cutoffCaptor.capture(), eq(SWEEP_BATCH_SIZE));
		then(gateway).should(never())
				.republish(any(UserAnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.isEmpty()).isTrue();
			softly.then(cutoffCaptor.getValue()).isBetween(before.minus(GRACE_PERIOD), after.minus(GRACE_PERIOD));
		});
	}

	@Test
	void givenAStaffEventLeftBehind_thenRepublishesIt(){
		Instant createdAt = Instant.now().minusSeconds(420L);
		UserAnalyticsEvent event = UserAnalyticsEvent.builder()
				.id("01EVENTSTAFFTERTINGGAL000")
				.aggregate(UserAnalyticsAggregate.STAFF)
				.type(UserAnalyticsEventType.STAFF_REGISTERED)
				.tenantId(TENANT_ID)
				.aggregateId("01STAFFWULANDARI000000000")
				.aggregateVersion(0)
				.payload("{\"role\":\"STAFF\"}")
				.status(UserAnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.version(0)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(event)).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		DefaultUserAnalyticsOutboxSweepUseCase useCase = new DefaultUserAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		UserAnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should()
				.republish(eq(event));
		then(gateway).should(never())
				.recordFailure(any(UserAnalyticsEvent.class));

		thenSoftly(softly -> {
			softly.then(response.republished()).isEqualTo(1);
			softly.then(response.failed()).isZero();
		});
	}

	@Test
	void givenRedisStillDown_thenBumpsTheAttemptCountAndKeepsTheRowCreated(){
		Instant createdAt = Instant.now().minusSeconds(3600L);
		UserAnalyticsEvent event = UserAnalyticsEvent.builder()
				.id("01EVENTCUSTOMERGAGAL00000")
				.aggregate(UserAnalyticsAggregate.CUSTOMER)
				.type(UserAnalyticsEventType.CUSTOMER_DELETED)
				.tenantId(TENANT_ID)
				.aggregateId("01CUSTOMERHARYANTO0000000")
				.aggregateVersion(2)
				.payload("{\"deleted\":true}")
				.status(UserAnalyticsEventStatus.CREATED)
				.occurredAt(createdAt)
				.attempts(4)
				.version(3)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(UserAnalyticsOutboxConstant.SWEEPER_ACTOR)
				.build();
		willReturn(List.of(event)).given(gateway)
				.findUnpublishedCreatedBefore(any(Instant.class), anyInt());
		willThrow(new IllegalStateException("Cannot get Jedis connection")).given(gateway)
				.republish(any(UserAnalyticsEvent.class));
		DefaultUserAnalyticsOutboxSweepUseCase useCase = new DefaultUserAnalyticsOutboxSweepUseCase(gateway, GRACE_PERIOD,
				SWEEP_BATCH_SIZE);

		UserAnalyticsOutboxSweepResponse response = useCase.execute();

		then(gateway).should()
				.recordFailure(eventCaptor.capture());

		UserAnalyticsEvent failed = eventCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(failed.attempts()).isEqualTo(5);
			softly.then(failed.status()).isEqualTo(UserAnalyticsEventStatus.CREATED);
			softly.then(failed.lastError()).contains("Cannot get Jedis connection");
			softly.then(response.republished()).isZero();
			softly.then(response.failed()).isEqualTo(1);
		});
	}

}
