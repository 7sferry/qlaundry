package com.ferry.order.core.service.update;

import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
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
public class DefaultLaundryServiceUpdateUseCase implements LaundryServiceUpdateUseCase{
	private static final double DEFAULT_EXPRESS_MULTIPLIER = 1.0d;

	private final LaundryServiceUpdateGateway gateway;

	@Override
	public void execute(LaundryServiceUpdateRequest request, OrderAuthPrincipal principal,
	                    LaundryServiceUpdatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new OrderForbiddenActionException("Only super staff can manage the service price list");
		}
		request.validate();
		LaundryServiceIdDomain serviceId = new LaundryServiceIdDomain(request.serviceId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		LaundryServiceDomain service = gateway.findById(serviceId, tenantId)
				.orElseThrow(() -> new NotFoundException("Service Not Found"));
		double expressMultiplier = request.expressMultiplier() == null
				? DEFAULT_EXPRESS_MULTIPLIER : request.expressMultiplier();
		boolean active = request.active() == null || request.active();
		LaundryServiceDomain saved = gateway.save(service.update(request.name(),
				new NoteDomain(request.description()), new MoneyDomain(request.pricePerUnit()), request.unit(),
				request.category(), request.estimatedHours(), expressMultiplier, request.popular(), active,
				principal.userId()));
		presenter.present(new LaundryServiceUpdateResponse(saved));
	}

}
