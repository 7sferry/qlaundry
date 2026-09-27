package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventRequest.LaundryServiceEvent;
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
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultLaundryServiceEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTSEROJABERSIH00000";
	private static final String SERVICE_ID = "01SERVICESETRIKAUAP000000";
	private static final String SECOND_SERVICE_ID = "01SERVICECUCISEPATU000000";

	@Mock
	LaundryServiceEventGateway gateway;
	@InjectMocks
	DefaultLaundryServiceEventUseCase useCase;
	@Mock
	LaundryServiceEventPresenter presenter;
	@Captor
	ArgumentCaptor<List<LaundryServiceSnapshot>> servicesCaptor;
	@Captor
	ArgumentCaptor<List<ConsumedEvent>> consumedCaptor;
	@Captor
	ArgumentCaptor<LaundryServiceEventResponse> responseCaptor;

	@Test
	void givenNoEventList_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new LaundryServiceEventRequest(null), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenMissingTypeOnOneEvent_thenRejectsItAndStillAppliesTheOther(){
		Instant now = Instant.now();
		LaundryServiceSnapshot steamIron = LaundryServiceSnapshot.builder()
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
		LaundryServiceSnapshot shoeWash = LaundryServiceSnapshot.builder()
				.tenantId(TENANT_ID)
				.serviceId(SECOND_SERVICE_ID)
				.name("Cuci Sepatu")
				.category("SHOES")
				.unit("ITEM")
				.pricePerUnit(BigDecimal.valueOf(40000))
				.estimatedHours(48)
				.expressMultiplier(1.25)
				.active(true)
				.version(1)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new LaundryServiceEventRequest(List.of(
				new LaundryServiceEvent("01EVENTSETRIKA00000000000", null, steamIron),
				new LaundryServiceEvent("01EVENTSEPATU000000000000", "LAUNDRY_SERVICE_UPDATED", shoeWash))), presenter);

		then(gateway).should()
				.upsert(servicesCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(servicesCaptor.getValue()).containsExactly(shoeWash);
			softly.then(responseCaptor.getValue().applied()).hasSize(1);
			softly.then(responseCaptor.getValue().rejected()).hasSize(1);
			softly.then(responseCaptor.getValue().rejected().getFirst().eventId())
					.isEqualTo("01EVENTSETRIKA00000000000");
		});
	}

	@Test
	void givenTwoServiceSnapshots_thenUpsertsBothInOneWriteAndRecordsBothConsumedEvents(){
		Instant now = Instant.now();
		LaundryServiceSnapshot deletedSteamIron = LaundryServiceSnapshot.builder()
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
		LaundryServiceSnapshot shoeWash = LaundryServiceSnapshot.builder()
				.tenantId(TENANT_ID)
				.serviceId(SECOND_SERVICE_ID)
				.name("Cuci Sepatu")
				.category("SHOES")
				.unit("ITEM")
				.pricePerUnit(BigDecimal.valueOf(40000))
				.estimatedHours(48)
				.expressMultiplier(1.25)
				.popular(true)
				.active(true)
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new LaundryServiceEventRequest(List.of(
				new LaundryServiceEvent("01EVENTSETRIKAHAPUS000000", "LAUNDRY_SERVICE_DELETED", deletedSteamIron),
				new LaundryServiceEvent("01EVENTSEPATUBARU00000000", "LAUNDRY_SERVICE_CREATED", shoeWash))), presenter);

		then(gateway).should()
				.upsert(servicesCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<ConsumedEvent> consumed = consumedCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(servicesCaptor.getValue()).containsExactly(deletedSteamIron, shoeWash);
			softly.then(consumed).extracting(ConsumedEvent::aggregate)
					.containsOnly(AnalyticsAggregate.LAUNDRY_SERVICE);
			softly.then(consumed).extracting(ConsumedEvent::aggregateId).containsExactly(SERVICE_ID, SECOND_SERVICE_ID);
			softly.then(consumed.getFirst().version()).isEqualTo(4);
			softly.then(responseCaptor.getValue().applied().getFirst().serviceId()).isEqualTo(SERVICE_ID);
			softly.then(responseCaptor.getValue().applied().getFirst().version()).isEqualTo(4);
			softly.then(responseCaptor.getValue().rejected()).isEmpty();
		});
	}

	@Test
	void givenEveryEventIsInvalid_thenNeverWrites(){
		Instant now = Instant.now();
		LaundryServiceSnapshot steamIron = LaundryServiceSnapshot.builder()
				.tenantId(TENANT_ID)
				.serviceId(SERVICE_ID)
				.name("Setrika Uap")
				.category("IRON")
				.unit("ITEM")
				.pricePerUnit(BigDecimal.valueOf(6000))
				.estimatedHours(12)
				.expressMultiplier(1.5)
				.active(true)
				.version(2)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new LaundryServiceEventRequest(List.of(
				new LaundryServiceEvent("", "LAUNDRY_SERVICE_UPDATED", steamIron))), presenter);

		then(gateway).should(never())
				.upsert(anyList());
		then(gateway).should(never())
				.recordConsumed(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().applied()).isEmpty();
			softly.then(responseCaptor.getValue().rejected()).hasSize(1);
		});
	}

}
