package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultLaundryServiceEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTSEROJABERSIH00000";
	private static final String SERVICE_ID = "01SERVICESETRIKAUAP000000";

	@Mock
	LaundryServiceEventGateway gateway;
	@InjectMocks
	DefaultLaundryServiceEventUseCase useCase;
	@Mock
	LaundryServiceEventPresenter presenter;
	@Captor
	ArgumentCaptor<ConsumedEvent> consumedCaptor;
	@Captor
	ArgumentCaptor<LaundryServiceEventResponse> responseCaptor;

	@Test
	void givenMissingType_thenThrowsConstraintViolationException(){
		Instant now = Instant.now();
		LaundryServiceSnapshot service = LaundryServiceSnapshot.builder()
				.tenantId(TENANT_ID)
				.serviceId(SERVICE_ID)
				.name("Setrika Uap")
				.category("IRON")
				.unit("ITEM")
				.pricePerUnit(BigDecimal.valueOf(6000))
				.estimatedHours(12)
				.expressMultiplier(1.5)
				.active(true)
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new LaundryServiceEventRequest("01EVENTSETRIKA00000000000", null, service),
								presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenDeletedServiceSnapshot_thenUpsertsItAndRecordsTheConsumedEvent(){
		Instant now = Instant.now();
		LaundryServiceSnapshot service = LaundryServiceSnapshot.builder()
				.tenantId(TENANT_ID)
				.serviceId(SERVICE_ID)
				.name("Setrika Uap")
				.category("IRON")
				.unit("ITEM")
				.pricePerUnit(BigDecimal.valueOf(6000))
				.estimatedHours(12)
				.expressMultiplier(1.5)
				.popular(false)
				.active(false)
				.deleted(true)
				.version(4)
				.createdAt(now.minusSeconds(864000L))
				.updatedAt(now)
				.build();

		useCase.execute(new LaundryServiceEventRequest("01EVENTSETRIKAHAPUS000000", "LAUNDRY_SERVICE_DELETED",
				service), presenter);

		then(gateway).should()
				.upsert(eq(service));
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(consumedCaptor.getValue().aggregate()).isEqualTo(AnalyticsAggregate.LAUNDRY_SERVICE);
			softly.then(consumedCaptor.getValue().aggregateId()).isEqualTo(SERVICE_ID);
			softly.then(consumedCaptor.getValue().version()).isEqualTo(4);
			softly.then(responseCaptor.getValue().serviceId()).isEqualTo(SERVICE_ID);
			softly.then(responseCaptor.getValue().version()).isEqualTo(4);
		});
	}

}
