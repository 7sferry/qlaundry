package com.ferry.order.core.service.update;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultLaundryServiceUpdateUseCase implements LaundryServiceUpdateUseCase{
	private static final double DEFAULT_EXPRESS_MULTIPLIER = 1.0d;

	private final LaundryServiceUpdateGateway gateway;
	private final OrderAnalyticsPublisher publisher;

	@Override
	public void execute(LaundryServiceUpdateRequest request, OrderAuthPrincipal principal,
	                    LaundryServiceUpdatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new OrderForbiddenActionException("Only super staff can manage the service price list");
		}
		request.validate();
		LaundryServiceId serviceId = new LaundryServiceId(request.serviceId());
		TenantId tenantId = new TenantId(principal.tenantId());
		LaundryService service = gateway.findById(serviceId, tenantId)
				.orElseThrow(() -> new NotFoundException("Service Not Found"));
		double expressMultiplier = request.expressMultiplier() == null
				? DEFAULT_EXPRESS_MULTIPLIER : request.expressMultiplier();
		boolean active = request.active() == null || request.active();
		LaundryService saved = gateway.save(service.update(request.name(),
				new Note(request.description()), new Money(request.pricePerUnit()), request.unit(),
				request.category(), request.estimatedHours(), expressMultiplier, request.popular(), active,
				principal.userId()));
		publisher.publish(publisher.save(AnalyticsEventConfig.laundryService(AnalyticsEventType.LAUNDRY_SERVICE_UPDATED,
				LaundryServiceAnalyticsMessage.from(saved), principal.userId())));
		presenter.present(new LaundryServiceUpdateResponse(saved));
	}

}
