package com.ferry.order.core.service.create;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultLaundryServiceCreateUseCase implements LaundryServiceCreateUseCase{
	private static final double DEFAULT_EXPRESS_MULTIPLIER = 1.0d;

	private final LaundryServiceCreateGateway gateway;
	private final AnalyticsEventPublisher publisher;

	@Override
	public void execute(LaundryServiceCreateRequest request, OrderAuthPrincipal principal,
	                    LaundryServiceCreatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new OrderForbiddenActionException("Only super staff can manage the service price list");
		}
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		double expressMultiplier = request.expressMultiplier() == null
				? DEFAULT_EXPRESS_MULTIPLIER : request.expressMultiplier();
		if(gateway.existsByName(request.name(), tenantId)){
			throw new IllegalArgumentException("Service name already exists");
		}
		LaundryServiceDomain saved = gateway.save(LaundryServiceDomain.create(tenantId.value(), request.name(),
				new NoteDomain(request.description()), new MoneyDomain(request.pricePerUnit()), request.unit(),
				request.category(), request.estimatedHours(), expressMultiplier, request.popular(),
				principal.userId()));
		publisher.publish(publisher.save(AnalyticsEventConfig.laundryService(AnalyticsEventType.LAUNDRY_SERVICE_CREATED,
				LaundryServiceAnalyticsMessage.from(saved), principal.userId())));
		presenter.present(new LaundryServiceCreateResponse(saved));
	}

}
