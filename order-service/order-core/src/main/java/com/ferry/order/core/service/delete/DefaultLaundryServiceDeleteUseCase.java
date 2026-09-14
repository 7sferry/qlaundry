package com.ferry.order.core.service.delete;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultLaundryServiceDeleteUseCase implements LaundryServiceDeleteUseCase{
	private final LaundryServiceDeleteGateway gateway;
	private final AnalyticsEventPublisher publisher;

	@Override
	public void execute(LaundryServiceDeleteRequest request, OrderAuthPrincipal principal,
	                    LaundryServiceDeletePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new OrderForbiddenActionException("Only super staff can manage the service price list");
		}
		request.validate();
		LaundryServiceIdDomain serviceId = new LaundryServiceIdDomain(request.serviceId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		LaundryServiceDomain service = gateway.findById(serviceId, tenantId)
				.orElseThrow(() -> new NotFoundException("Service Not Found"));
		if(gateway.hasOpenOrders(serviceId, tenantId)){
			throw new OrderForbiddenActionException("Cannot delete a service that still has orders in progress");
		}
		LaundryServiceDomain saved = gateway.save(service.markDeleted(principal.userId()));
		publisher.publish(publisher.save(AnalyticsEventConfig.laundryService(AnalyticsEventType.LAUNDRY_SERVICE_DELETED,
				LaundryServiceAnalyticsMessage.from(saved), principal.userId())));
		presenter.present(new LaundryServiceDeleteResponse(service.id()));
	}

}
