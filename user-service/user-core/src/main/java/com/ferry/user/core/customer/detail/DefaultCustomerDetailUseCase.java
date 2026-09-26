package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerDetailUseCase implements CustomerDetailUseCase{
	private final CustomerDetailGateway gateway;

	@Override
	public void execute(CustomerDetailRequest request, UserAuthPrincipal principal,
	                    CustomerDetailPresenter presenter){
		request.validate();
		CustomerId customerId = new CustomerId(request.customerId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Customer customer = gateway.findById(customerId, tenantId)
				.orElseThrow(() -> new NotFoundException("Customer Not Found"));
		CustomerEmailFilter emailFilter = CustomerEmailFilter.builder()
				.customerId(customer.id())
				.build();
		List<CustomerEmail> emails = gateway.findEmailsByFilter(emailFilter);
		CustomerPhoneFilter phoneFilter = CustomerPhoneFilter.builder()
				.customerId(customer.id())
				.build();
		List<CustomerPhone> phones = gateway.findPhonesByFilter(phoneFilter);
		CustomerAddressFilter addressFilter = CustomerAddressFilter.builder()
				.customerId(customer.id())
				.build();
		List<CustomerAddress> addresses = gateway.findAddressesByFilter(addressFilter);
		presenter.present(new CustomerDetailResponse(customer, emails, phones, addresses));
	}

}
