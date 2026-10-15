package com.ferry.analytics.core.event.customer;

import com.ferry.analytics.core.event.customer.CustomerEventRequest.CustomerEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.CustomerSnapshot;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTCEMPAKABIRU000000";
	private static final String CUSTOMER_ID = "01CUSTOMERSITINURHAYA0000";
	private static final String SECOND_CUSTOMER_ID = "01CUSTOMERAGUSPRASETYO000";

	@Mock
	CustomerEventGateway gateway;
	@InjectMocks
	DefaultCustomerEventUseCase useCase;
	@Mock
	CustomerEventPresenter presenter;
	@Captor
	ArgumentCaptor<List<CustomerSnapshot>> customersCaptor;
	@Captor
	ArgumentCaptor<List<ConsumedEvent>> consumedCaptor;
	@Captor
	ArgumentCaptor<CustomerEventResponse> responseCaptor;

	@Test
	void givenNoEventList_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new CustomerEventRequest(null), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenRegisteredAndDeletedCustomers_thenUpsertsBothInOneWrite(){
		Instant now = Instant.now();
		CustomerSnapshot siti = CustomerSnapshot.builder()
				.tenantId(TENANT_ID)
				.customerId(CUSTOMER_ID)
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();
		CustomerSnapshot deletedAgus = CustomerSnapshot.builder()
				.tenantId(TENANT_ID)
				.customerId(SECOND_CUSTOMER_ID)
				.deleted(true)
				.version(2)
				.createdAt(now.minusSeconds(604800L))
				.updatedAt(now)
				.build();

		useCase.execute(new CustomerEventRequest(List.of(
				new CustomerEvent("01EVENTSITIBARU0000000000", "CUSTOMER_REGISTERED", siti),
				new CustomerEvent("01EVENTAGUSHAPUS000000000", "CUSTOMER_DELETED", deletedAgus))), presenter);

		then(gateway).should()
				.upsert(customersCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<ConsumedEvent> consumed = consumedCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(customersCaptor.getValue()).containsExactly(siti, deletedAgus);
			softly.then(consumed).extracting(ConsumedEvent::aggregate).containsOnly(AnalyticsAggregate.CUSTOMER);
			softly.then(consumed).extracting(ConsumedEvent::aggregateId)
					.containsExactly(CUSTOMER_ID, SECOND_CUSTOMER_ID);
			softly.then(responseCaptor.getValue().applied().getLast().version()).isEqualTo(2);
			softly.then(responseCaptor.getValue().rejected()).isEmpty();
		});
	}

	@Test
	void givenEveryEventIsInvalid_thenNeverWrites(){
		Instant now = Instant.now();
		CustomerSnapshot siti = CustomerSnapshot.builder()
				.tenantId(TENANT_ID)
				.customerId(CUSTOMER_ID)
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new CustomerEventRequest(List.of(
				new CustomerEvent("01EVENTSITITANPATIPE00000", "", siti))), presenter);

		then(gateway).should(never())
				.upsert(anyList());
		then(gateway).should(never())
				.recordConsumed(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().applied()).isEmpty();
			softly.then(responseCaptor.getValue().rejected()).extracting(r -> r.eventId())
					.containsExactly("01EVENTSITITANPATIPE00000");
		});
	}

}
