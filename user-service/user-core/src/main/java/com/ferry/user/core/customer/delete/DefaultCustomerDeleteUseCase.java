package com.ferry.user.core.customer.delete;

import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerDeleteUseCase implements CustomerDeleteUseCase{
	private final CustomerDeleteGateway gateway;

	@Override
	public void execute(CustomerDeleteRequest request, UserAuthPrincipal principal, CustomerDeletePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can delete customer");
		}
		request.validate();
		CustomerIdDomain customerId = new CustomerIdDomain(request.customerId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		CustomerDomain customer = gateway.findById(customerId, tenantId)
				.orElseThrow(() -> new NotFoundException("Customer Not Found"));
		gateway.save(customer.markDeleted(principal.userId()));
		gateway.deleteContacts(customer.id(), principal.userId());
		presenter.present(new CustomerDeleteResponse(customer.id()));
	}

}
