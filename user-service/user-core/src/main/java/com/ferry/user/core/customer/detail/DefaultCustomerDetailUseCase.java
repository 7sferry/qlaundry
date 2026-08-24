package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantIdDomain;
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
		CustomerIdDomain customerId = new CustomerIdDomain(request.customerId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		CustomerDomain customer = gateway.findById(customerId, tenantId)
				.orElseThrow(() -> new NotFoundException("Customer Not Found"));
		CustomerEmailFilter emailFilter = CustomerEmailFilter.builder()
				.customerId(customer.id())
				.build();
		List<CustomerEmailDomain> emails = gateway.findEmailsByFilter(emailFilter);
		CustomerPhoneFilter phoneFilter = CustomerPhoneFilter.builder()
				.customerId(customer.id())
				.build();
		List<CustomerPhoneDomain> phones = gateway.findPhonesByFilter(phoneFilter);
		CustomerAddressFilter addressFilter = CustomerAddressFilter.builder()
				.customerId(customer.id())
				.build();
		List<CustomerAddressDomain> addresses = gateway.findAddressesByFilter(addressFilter);
		presenter.present(new CustomerDetailResponse(customer, emails, phones, addresses));
	}

}
