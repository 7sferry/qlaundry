package com.ferry.analytics.core.event.staff;

import com.ferry.analytics.core.event.staff.StaffEventRequest.StaffEvent;
import com.ferry.analytics.core.event.staff.StaffEventResponse.AppliedStaffEvent;
import com.ferry.analytics.core.event.staff.StaffEventResponse.RejectedStaffEvent;
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
public class DefaultStaffEventUseCase implements StaffEventUseCase{
	private final StaffEventGateway gateway;

	@Override
	public void execute(StaffEventRequest request, StaffEventPresenter presenter){
		request.validate();
		List<StaffEvent> accepted = new ArrayList<>();
		List<RejectedStaffEvent> rejected = new ArrayList<>();
		for(StaffEvent event : request.events()){
			rejectionOf(event).ifPresentOrElse(
					reason -> rejected.add(new RejectedStaffEvent(event.eventId(), reason)),
					() -> accepted.add(event));
		}
		if(!accepted.isEmpty()){
			gateway.upsert(accepted.stream().map(StaffEvent::staff).toList());
			gateway.recordConsumed(accepted.stream()
					.map(event -> ConsumedEvent.consumed(event.eventId(), AnalyticsAggregate.STAFF, event.type(),
							event.staff().tenantId(), event.staff().staffId(), event.staff().version()))
					.toList());
		}
		presenter.present(new StaffEventResponse(
				accepted.stream()
						.map(event -> new AppliedStaffEvent(event.eventId(), event.staff().staffId(),
								event.staff().version()))
						.toList(),
				rejected));
	}

	private Optional<String> rejectionOf(StaffEvent event){
		try{
			event.validate();
			return Optional.empty();
		}catch(ConstraintViolationException e){
			return Optional.of(e.getMessage());
		}
	}

}
