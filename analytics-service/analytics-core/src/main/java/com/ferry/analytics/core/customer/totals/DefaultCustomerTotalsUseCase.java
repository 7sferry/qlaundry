package com.ferry.analytics.core.customer.totals;

import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerTotalsUseCase implements CustomerTotalsUseCase{
	private final CustomerTotalsGateway gateway;

	@Override
	public void execute(CustomerTotalsRequest request, AnalyticsAuthPrincipal principal,
	                    CustomerTotalsPresenter presenter){
		request.validate();
		Set<String> customerIds = request.sanitizedCustomerIds();
		if(customerIds.isEmpty()){
			presenter.present(new CustomerTotalsResponse(List.of()));
			return;
		}
		TenantId tenantId = new TenantId(principal.tenantId());
		presenter.present(new CustomerTotalsResponse(gateway.totals(tenantId, customerIds)));
	}

}
