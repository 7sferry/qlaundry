package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
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
public class DefaultCustomerRegistrationUseCase implements CustomerRegistrationUseCase{
	private final CustomerRegistrationGateway gateway;

	@Override
	public void execute(CustomerRegistrationRequest request, UserAuthPrincipal principal,
	                    CustomerRegistrationPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		FullName fullName = new FullName(request.fullName());
		Description notes = new Description(request.notes());
		Customer saved = gateway.save(Customer.register(tenantId.value(), fullName, notes,
				principal.userId()));
		List<CustomerPhone> phones = List.of(gateway.save(CustomerPhone.register(saved.id(),
				new Phone(request.phone()), principal.userId())));
		List<CustomerEmail> emails = saveEmail(request, saved, principal);
		List<CustomerAddress> addresses = saveAddress(request, saved, principal);
		presenter.present(new CustomerRegistrationResponse(saved, emails, phones, addresses));
	}

	private List<CustomerEmail> saveEmail(CustomerRegistrationRequest request, Customer customer,
	                                            UserAuthPrincipal principal){
		if(request.email() == null || request.email().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerEmail.register(customer.id(), new Email(request.email()),
				principal.userId())));
	}

	private List<CustomerAddress> saveAddress(CustomerRegistrationRequest request, Customer customer,
	                                                UserAuthPrincipal principal){
		if(request.address() == null || request.address().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerAddress.register(customer.id(),
				new AddressLine(request.address()), principal.userId())));
	}

}
