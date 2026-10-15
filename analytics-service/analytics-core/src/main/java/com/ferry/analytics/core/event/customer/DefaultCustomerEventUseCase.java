package com.ferry.analytics.core.event.customer;

import com.ferry.analytics.core.event.customer.CustomerEventRequest.CustomerEvent;
import com.ferry.analytics.core.event.customer.CustomerEventResponse.AppliedCustomerEvent;
import com.ferry.analytics.core.event.customer.CustomerEventResponse.RejectedCustomerEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerEventUseCase implements CustomerEventUseCase{
	private final CustomerEventGateway gateway;

	@Override
	public void execute(CustomerEventRequest request, CustomerEventPresenter presenter){
		request.validate();
		List<CustomerEvent> accepted = new ArrayList<>();
		List<RejectedCustomerEvent> rejected = new ArrayList<>();
		for(CustomerEvent event : request.events()){
			rejectionOf(event).ifPresentOrElse(
					reason -> rejected.add(new RejectedCustomerEvent(event.eventId(), reason)),
					() -> accepted.add(event));
		}
		if(!accepted.isEmpty()){
			gateway.upsert(accepted.stream().map(CustomerEvent::customer).toList());
			gateway.recordConsumed(accepted.stream()
					.map(event -> ConsumedEvent.consumed(event.eventId(), AnalyticsAggregate.CUSTOMER, event.type(),
							event.customer().tenantId(), event.customer().customerId(), event.customer().version()))
					.toList());
		}
		presenter.present(new CustomerEventResponse(
				accepted.stream()
						.map(event -> new AppliedCustomerEvent(event.eventId(), event.customer().customerId(),
								event.customer().version()))
						.toList(),
				rejected));
	}

	private Optional<String> rejectionOf(CustomerEvent event){
		try{
			event.validate();
			return Optional.empty();
		}catch(ConstraintViolationException e){
			return Optional.of(e.getMessage());
		}
	}

}
