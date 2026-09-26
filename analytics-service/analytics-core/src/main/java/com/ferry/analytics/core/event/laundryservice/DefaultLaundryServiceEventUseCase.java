package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;
import lombok.RequiredArgsConstructor;

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
		LaundryServiceSnapshot service = request.service();
		gateway.upsert(service);
		gateway.recordConsumed(ConsumedEvent.consumed(request.eventId(), AnalyticsAggregate.LAUNDRY_SERVICE,
				request.type(), service.tenantId(), service.serviceId(), service.version()));
		presenter.present(new LaundryServiceEventResponse(request.eventId(), service.serviceId(), service.version()));
	}

}
