package com.ferry.user.core.customer.delete;

import com.ferry.user.core.analytics.UserAnalyticsEvent;
import com.ferry.user.core.analytics.CustomerAnalyticsMessage;
import com.ferry.user.core.analytics.UserAnalyticsPublisher;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerDeleteUseCase implements CustomerDeleteUseCase{
	private final CustomerDeleteGateway gateway;
	private final UserAnalyticsPublisher analyticsPublisher;

	@Override
	public void execute(CustomerDeleteRequest request, UserAuthPrincipal principal, CustomerDeletePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can delete customer");
		}
		request.validate();
		CustomerId customerId = new CustomerId(request.customerId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Customer customer = gateway.findById(customerId, tenantId)
				.orElseThrow(() -> new NotFoundException("Customer Not Found"));
		Customer deleted = gateway.save(customer.markDeleted(principal.userId()));
		gateway.deleteContacts(customer.id(), principal.userId());
		analyticsPublisher.publish(analyticsPublisher.save(UserAnalyticsEvent.customer(
				UserAnalyticsEventType.CUSTOMER_DELETED, CustomerAnalyticsMessage.from(deleted), principal.userId())));
		presenter.present(new CustomerDeleteResponse(customer.id()));
	}

}
