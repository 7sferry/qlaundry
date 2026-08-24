package com.ferry.user.core.customer.update;

import com.ferry.user.domain.common.AddressLineDomain;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
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
public class DefaultCustomerUpdateUseCase implements CustomerUpdateUseCase{
	private final CustomerUpdateGateway gateway;

	@Override
	public void execute(CustomerUpdateRequest request, UserAuthPrincipal principal,
	                    CustomerUpdatePresenter presenter){
		request.validate();
		CustomerIdDomain customerId = new CustomerIdDomain(request.customerId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		CustomerDomain customer = gateway.findById(customerId, tenantId)
				.orElseThrow(() -> new NotFoundException("Customer Not Found"));
		FullNameDomain fullName = new FullNameDomain(request.fullName());
		DescriptionDomain notes = new DescriptionDomain(request.notes());
		CustomerDomain saved = gateway.save(customer.update(fullName, notes, principal.userId()));
		List<CustomerPhoneDomain> phones = replacePhone(request, saved, principal);
		List<CustomerEmailDomain> emails = replaceEmail(request, saved, principal);
		List<CustomerAddressDomain> addresses = replaceAddress(request, saved, principal);
		presenter.present(new CustomerUpdateResponse(saved, emails, phones, addresses));
	}

	private List<CustomerPhoneDomain> replacePhone(CustomerUpdateRequest request, CustomerDomain customer,
	                                               UserAuthPrincipal principal){
		gateway.deletePhones(customer.id(), principal.userId());
		return List.of(gateway.save(CustomerPhoneDomain.register(customer.id(), new PhoneDomain(request.phone()),
				principal.userId())));
	}

	private List<CustomerEmailDomain> replaceEmail(CustomerUpdateRequest request, CustomerDomain customer,
	                                               UserAuthPrincipal principal){
		gateway.deleteEmails(customer.id(), principal.userId());
		if(request.email() == null || request.email().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerEmailDomain.register(customer.id(), new EmailDomain(request.email()),
				principal.userId())));
	}

	private List<CustomerAddressDomain> replaceAddress(CustomerUpdateRequest request, CustomerDomain customer,
	                                                   UserAuthPrincipal principal){
		gateway.deleteAddresses(customer.id(), principal.userId());
		if(request.address() == null || request.address().isBlank()){
			return List.of();
		}
		return List.of(gateway.save(CustomerAddressDomain.register(customer.id(),
				new AddressLineDomain(request.address()), principal.userId())));
	}

}
