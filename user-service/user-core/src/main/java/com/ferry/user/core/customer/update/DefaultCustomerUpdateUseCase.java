package com.ferry.user.core.customer.update;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerUpdateUseCase implements CustomerUpdateUseCase{
	private final CustomerUpdateGateway gateway;

	@Override
	public void execute(CustomerUpdateRequest request, UserAuthPrincipal principal,
	                    CustomerUpdatePresenter presenter){
		request.validate();
		CustomerId customerId = new CustomerId(request.customerId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Customer customer = gateway.findById(customerId, tenantId)
				.orElseThrow(() -> new NotFoundException("Customer Not Found"));
		FullName fullName = new FullName(request.fullName());
		Description notes = new Description(request.notes());
		Customer saved = gateway.save(customer.update(fullName, notes, principal.userId()));
		List<CustomerPhone> phones = replacePhone(request, saved, principal);
		List<CustomerEmail> emails = replaceEmail(request, saved, principal);
		List<CustomerAddress> addresses = replaceAddress(request, saved, principal);
		presenter.present(new CustomerUpdateResponse(saved, emails, phones, addresses));
	}

	private List<CustomerPhone> replacePhone(CustomerUpdateRequest request, Customer customer,
	                                               UserAuthPrincipal principal){
		gateway.deletePhones(customer.id(), principal.userId());
		return List.of(gateway.save(CustomerPhone.register(customer.id(), new Phone(request.phone()),
				principal.userId())));
	}

	private List<CustomerEmail> replaceEmail(CustomerUpdateRequest request, Customer customer,
	                                               UserAuthPrincipal principal){
		gateway.deleteEmails(customer.id(), principal.userId());
		if(request.email() == null || request.email().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerEmail.register(customer.id(), new Email(request.email()),
				principal.userId())));
	}

	private List<CustomerAddress> replaceAddress(CustomerUpdateRequest request, Customer customer,
	                                                   UserAuthPrincipal principal){
		gateway.deleteAddresses(customer.id(), principal.userId());
		if(request.address() == null || request.address().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerAddress.register(customer.id(),
				new AddressLine(request.address()), principal.userId())));
	}

}
