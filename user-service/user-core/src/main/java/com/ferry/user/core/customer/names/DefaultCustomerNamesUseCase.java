package com.ferry.user.core.customer.names;

import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerNamesUseCase implements CustomerNamesUseCase{
	private final CustomerNamesGateway gateway;

	@Override
	public void execute(CustomerNamesRequest request, UserAuthPrincipal principal, CustomerNamesPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		Set<String> customerIds = request.sanitizedCustomerIds();
		if(customerIds.isEmpty()){
			presenter.present(new CustomerNamesResponse(List.of()));
			return;
		}
		presenter.present(new CustomerNamesResponse(gateway.findNames(tenantId, customerIds)));
	}

}
