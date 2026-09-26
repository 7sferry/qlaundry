package com.ferry.order.core.order.saga;

import com.ferry.order.core.order.constant.OrderPromotionSagaConstant;
import com.ferry.order.core.order.create.OrderPromotionGateway;
import com.ferry.order.core.order.create.PromotionReleaseHttpRequest;
import com.ferry.order.domain.common.exception.PromotionUnavailableException;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPromotionSaga;
import com.ferry.order.domain.order.OrderPromotionSagaStatus;
import com.ferry.order.domain.tenant.TenantId;
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
class DefaultOrderPromotionSagaSweepUseCaseTest{

	private static final String TENANT_ID = "01TENANTFLAMBOYAN00000000";
	private static final String STAFF_ID = "01STAFFYOGAPERMANA000000";
	private static final String ORDER_NUMBER = "INV-20260913-8KD41MZQ2R7XA";
	private static final Duration GRACE_PERIOD = Duration.ofMinutes(90);
	private static final int SWEEP_BATCH_SIZE = 100;

	@Mock
	OrderPromotionSagaSweepGateway gateway;
	@Mock
	OrderPromotionGateway promotionGateway;
	@Captor
	ArgumentCaptor<Instant> cutoffCaptor;
	@Captor
	ArgumentCaptor<OrderPromotionSaga> sagaCaptor;
	@Captor
	ArgumentCaptor<PromotionReleaseHttpRequest> releaseCaptor;

	@Test
	void givenNoStalePendingSaga_thenReturnsAnEmptySweepLookingBackOnlyPastTheGracePeriod(){
		DefaultOrderPromotionSagaSweepUseCase useCase = new DefaultOrderPromotionSagaSweepUseCase(gateway,
				promotionGateway, GRACE_PERIOD, SWEEP_BATCH_SIZE);
		willReturn(List.of()).given(gateway)
				.findPendingUntouchedSince(any(Instant.class), anyInt());
		Instant before = Instant.now();

		OrderPromotionSagaSweepResponse response = useCase.execute();

		Instant after = Instant.now();
		then(gateway).should()
				.findPendingUntouchedSince(cutoffCaptor.capture(), eq(SWEEP_BATCH_SIZE));
		then(promotionGateway).shouldHaveNoInteractions();

		thenSoftly(softly -> {
			softly.then(response.isEmpty()).isTrue();
			softly.then(cutoffCaptor.getValue())
					.isBetween(before.minus(GRACE_PERIOD), after.minus(GRACE_PERIOD));
		});
	}

	@Test
	void givenPendingSagaWhoseOrderCommitted_thenMarksItCommittedWithoutReleasing(){
		Instant createdAt = Instant.now().minusSeconds(900L);
		OrderPromotionSaga saga = OrderPromotionSaga.builder()
				.id("01SAGAKOMITTERLAMBAT00000")
				.tenantId(TENANT_ID)
				.referenceId(ORDER_NUMBER)
				.status(OrderPromotionSagaStatus.PENDING)
				.attempts(0)
				.version(0)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(saga)).given(gateway)
				.findPendingUntouchedSince(any(Instant.class), anyInt());
		willReturn(true).given(gateway)
				.orderExists(any(OrderNumber.class), any(TenantId.class));
		DefaultOrderPromotionSagaSweepUseCase useCase = new DefaultOrderPromotionSagaSweepUseCase(gateway,
				promotionGateway, GRACE_PERIOD, SWEEP_BATCH_SIZE);

		OrderPromotionSagaSweepResponse response = useCase.execute();

		then(gateway).should()
				.orderExists(eq(new OrderNumber(ORDER_NUMBER)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.markCommitted(sagaCaptor.capture());
		then(gateway).should(never())
				.markReleased(any(OrderPromotionSaga.class));
		then(promotionGateway).shouldHaveNoInteractions();

		thenSoftly(softly -> {
			softly.then(sagaCaptor.getValue().status()).isEqualTo(OrderPromotionSagaStatus.COMMITTED);
			softly.then(sagaCaptor.getValue().updatedBy()).isEqualTo(OrderPromotionSagaConstant.SWEEPER_ACTOR);
			softly.then(sagaCaptor.getValue().createdBy()).isEqualTo(STAFF_ID);
			softly.then(response.committed()).isEqualTo(1);
			softly.then(response.released()).isZero();
		});
	}

	@Test
	void givenPendingSagaWithoutAnOrder_thenReleasesTheClaimAndMarksItReleased(){
		Instant createdAt = Instant.now().minusSeconds(1800L);
		OrderPromotionSaga saga = OrderPromotionSaga.builder()
				.id("01SAGAYATIMPIATUDIPROSES")
				.tenantId(TENANT_ID)
				.referenceId(ORDER_NUMBER)
				.status(OrderPromotionSagaStatus.PENDING)
				.attempts(0)
				.version(0)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(saga)).given(gateway)
				.findPendingUntouchedSince(any(Instant.class), anyInt());
		willReturn(false).given(gateway)
				.orderExists(any(OrderNumber.class), any(TenantId.class));
		DefaultOrderPromotionSagaSweepUseCase useCase = new DefaultOrderPromotionSagaSweepUseCase(gateway,
				promotionGateway, GRACE_PERIOD, SWEEP_BATCH_SIZE);

		OrderPromotionSagaSweepResponse response = useCase.execute();

		then(promotionGateway).should()
				.release(releaseCaptor.capture());
		then(gateway).should()
				.markReleased(sagaCaptor.capture());
		then(gateway).should(never())
				.markCommitted(any(OrderPromotionSaga.class));
		then(gateway).should(never())
				.recordFailure(any(OrderPromotionSaga.class));

		PromotionReleaseHttpRequest release = releaseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(release.tenantId()).isEqualTo(TENANT_ID);
			softly.then(release.referenceId()).isEqualTo(ORDER_NUMBER);
			softly.then(release.releasedBy()).isEqualTo(OrderPromotionSagaConstant.SWEEPER_ACTOR);
			softly.then(sagaCaptor.getValue().status()).isEqualTo(OrderPromotionSagaStatus.RELEASED);
			softly.then(sagaCaptor.getValue().referenceId()).isEqualTo(ORDER_NUMBER);
			softly.then(response.released()).isEqualTo(1);
		});
	}

	@Test
	void givenReleaseFails_thenRecordsTheFailureAndLeavesTheSagaPending(){
		Instant createdAt = Instant.now().minusSeconds(3600L);
		OrderPromotionSaga saga = OrderPromotionSaga.builder()
				.id("01SAGAPROMOSERVICEMATI00")
				.tenantId(TENANT_ID)
				.referenceId(ORDER_NUMBER)
				.status(OrderPromotionSagaStatus.PENDING)
				.attempts(2)
				.lastError("java.net.ConnectException: Connection refused")
				.version(2)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt.plusSeconds(600L))
				.updatedBy(OrderPromotionSagaConstant.SWEEPER_ACTOR)
				.build();
		willReturn(List.of(saga)).given(gateway)
				.findPendingUntouchedSince(any(Instant.class), anyInt());
		willReturn(false).given(gateway)
				.orderExists(any(OrderNumber.class), any(TenantId.class));
		willThrow(new PromotionUnavailableException("Promotion service is unavailable. Please try again.",
				new RuntimeException("connect timed out"))).given(promotionGateway)
				.release(any(PromotionReleaseHttpRequest.class));
		DefaultOrderPromotionSagaSweepUseCase useCase = new DefaultOrderPromotionSagaSweepUseCase(gateway,
				promotionGateway, GRACE_PERIOD, SWEEP_BATCH_SIZE);

		OrderPromotionSagaSweepResponse response = useCase.execute();

		then(gateway).should()
				.recordFailure(sagaCaptor.capture());
		then(gateway).should(never())
				.markReleased(any(OrderPromotionSaga.class));

		OrderPromotionSaga failed = sagaCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(failed.status()).isEqualTo(OrderPromotionSagaStatus.PENDING);
			softly.then(failed.attempts()).isEqualTo(3);
			softly.then(failed.lastError())
					.contains(PromotionUnavailableException.class.getName())
					.contains("Promotion service is unavailable. Please try again.");
			softly.then(response.failed()).isEqualTo(1);
			softly.then(response.released()).isZero();
		});
	}

	@Test
	void givenOneReleaseFailsInABatch_thenTheRestOfTheBatchIsStillSwept(){
		Instant createdAt = Instant.now().minusSeconds(1200L);
		OrderPromotionSaga first = OrderPromotionSaga.builder()
				.id("01SAGAPERTAMAGAGAL000000")
				.tenantId(TENANT_ID)
				.referenceId("INV-20260913-3PX9WQ0M1TB4E")
				.status(OrderPromotionSagaStatus.PENDING)
				.attempts(0)
				.version(0)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		OrderPromotionSaga second = OrderPromotionSaga.builder()
				.id("01SAGAKEDUABERHASIL00000")
				.tenantId("01TENANTKAMBOJA000000000")
				.referenceId("INV-20260913-6RJ2NF8V0HC5K")
				.status(OrderPromotionSagaStatus.PENDING)
				.attempts(0)
				.version(0)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy("01STAFFLESTARIAYU0000000")
				.updatedAt(createdAt)
				.updatedBy("01STAFFLESTARIAYU0000000")
				.build();
		willReturn(List.of(first, second)).given(gateway)
				.findPendingUntouchedSince(any(Instant.class), anyInt());
		willReturn(false).given(gateway)
				.orderExists(any(OrderNumber.class), any(TenantId.class));
		willThrow(new IllegalStateException("promotion-service returned 502")).willDoNothing().given(promotionGateway)
				.release(any(PromotionReleaseHttpRequest.class));
		DefaultOrderPromotionSagaSweepUseCase useCase = new DefaultOrderPromotionSagaSweepUseCase(gateway,
				promotionGateway, GRACE_PERIOD, SWEEP_BATCH_SIZE);

		OrderPromotionSagaSweepResponse response = useCase.execute();

		then(promotionGateway).should(times(2))
				.release(releaseCaptor.capture());
		then(gateway).should()
				.recordFailure(any(OrderPromotionSaga.class));
		then(gateway).should()
				.markReleased(sagaCaptor.capture());

		thenSoftly(softly -> {
			softly.then(releaseCaptor.getAllValues().getLast().tenantId()).isEqualTo("01TENANTKAMBOJA000000000");
			softly.then(sagaCaptor.getValue().referenceId()).isEqualTo("INV-20260913-6RJ2NF8V0HC5K");
			softly.then(response.released()).isEqualTo(1);
			softly.then(response.failed()).isEqualTo(1);
			softly.then(response.committed()).isZero();
		});
	}

}
