package com.ferry.user.core.customer.verification;

import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerVerificationUseCase implements CustomerVerificationUseCase{
	private final CustomerVerificationGateway gateway;

	@Override
	public void execute(CustomerVerificationRequest request, CustomerVerificationPresenter presenter){
		request.validate();
		CustomerIdDomain customerId = new CustomerIdDomain(request.customerId());
		TenantIdDomain tenantId = new TenantIdDomain(request.tenantId());
		boolean valid = gateway.existsByIdAndTenantId(customerId, tenantId);
		presenter.present(new CustomerVerificationResponse(customerId.value(), tenantId.value(), valid));
	}

}
