package com.ferry.user.core.customer.verification;

import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.tenant.TenantId;
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
		CustomerId customerId = new CustomerId(request.customerId());
		TenantId tenantId = new TenantId(request.tenantId());
		boolean valid = gateway.existsByIdAndTenantId(customerId, tenantId);
		presenter.present(new CustomerVerificationResponse(customerId.value(), tenantId.value(), valid));
	}

}
