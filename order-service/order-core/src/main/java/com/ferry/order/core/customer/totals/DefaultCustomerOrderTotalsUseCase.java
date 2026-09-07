package com.ferry.order.core.customer.totals;

import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerOrderTotalsUseCase implements CustomerOrderTotalsUseCase{
	private final CustomerOrderTotalsGateway gateway;

	@Override
	public void execute(CustomerOrderTotalsRequest request, OrderAuthPrincipal principal, CustomerOrderTotalsPresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		presenter.present(new CustomerOrderTotalsResponse(gateway.findTotals(request.customerIds(), tenantId)));
	}

}
