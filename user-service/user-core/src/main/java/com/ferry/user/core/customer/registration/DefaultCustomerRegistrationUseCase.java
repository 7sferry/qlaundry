package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.common.AddressLineDomain;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
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
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		FullNameDomain fullName = new FullNameDomain(request.fullName());
		DescriptionDomain notes = new DescriptionDomain(request.notes());
		CustomerDomain saved = gateway.save(CustomerDomain.register(tenantId.value(), fullName, notes,
				principal.userId()));
		List<CustomerPhoneDomain> phones = List.of(gateway.save(CustomerPhoneDomain.register(saved.id(),
				new PhoneDomain(request.phone()), principal.userId())));
		List<CustomerEmailDomain> emails = saveEmail(request, saved, principal);
		List<CustomerAddressDomain> addresses = saveAddress(request, saved, principal);
		presenter.present(new CustomerRegistrationResponse(saved, emails, phones, addresses));
	}

	private List<CustomerEmailDomain> saveEmail(CustomerRegistrationRequest request, CustomerDomain customer,
	                                            UserAuthPrincipal principal){
		if(request.email() == null || request.email().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerEmailDomain.register(customer.id(), new EmailDomain(request.email()),
				principal.userId())));
	}

	private List<CustomerAddressDomain> saveAddress(CustomerRegistrationRequest request, CustomerDomain customer,
	                                                UserAuthPrincipal principal){
		if(request.address() == null || request.address().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerAddressDomain.register(customer.id(),
				new AddressLineDomain(request.address()), principal.userId())));
	}

}
