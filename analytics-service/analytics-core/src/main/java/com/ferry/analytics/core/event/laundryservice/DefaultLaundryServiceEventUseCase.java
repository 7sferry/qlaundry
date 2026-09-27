package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventRequest.LaundryServiceEvent;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventResponse.AppliedLaundryServiceEvent;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventResponse.RejectedLaundryServiceEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultLaundryServiceEventUseCase implements LaundryServiceEventUseCase{
	private final LaundryServiceEventGateway gateway;

	@Override
	public void execute(LaundryServiceEventRequest request, LaundryServiceEventPresenter presenter){
		request.validate();
		List<LaundryServiceEvent> accepted = new ArrayList<>();
		List<RejectedLaundryServiceEvent> rejected = new ArrayList<>();
		for(LaundryServiceEvent event : request.events()){
			rejectionOf(event).ifPresentOrElse(
					reason -> rejected.add(new RejectedLaundryServiceEvent(event.eventId(), reason)),
					() -> accepted.add(event));
		}
		if(!accepted.isEmpty()){
			gateway.upsert(accepted.stream().map(LaundryServiceEvent::service).toList());
			gateway.recordConsumed(accepted.stream()
					.map(event -> ConsumedEvent.consumed(event.eventId(), AnalyticsAggregate.LAUNDRY_SERVICE,
							event.type(), event.service().tenantId(), event.service().serviceId(),
							event.service().version()))
					.toList());
		}
		presenter.present(new LaundryServiceEventResponse(
				accepted.stream()
						.map(event -> new AppliedLaundryServiceEvent(event.eventId(), event.service().serviceId(),
								event.service().version()))
						.toList(),
				rejected));
	}

	private Optional<String> rejectionOf(LaundryServiceEvent event){
		try{
			event.validate();
			return Optional.empty();
		}catch(ConstraintViolationException e){
			return Optional.of(e.getMessage());
		}
	}

}
