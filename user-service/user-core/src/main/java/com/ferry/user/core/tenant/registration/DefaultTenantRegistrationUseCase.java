package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.tenant.TenantDomain;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantRegistrationUseCase implements TenantRegistrationUseCase{
	private final TenantRegistrationGateway gateway;

	@Override
	public void execute(TenantRegistrationRequest request, TenantRegistrationPresenter presenter){
		TenantDomain savedTenant = saveTenant(request);
		StaffRegistrationResponse registeredAdmin = registerAdmin(request, savedTenant);
		presenter.present(new TenantRegistrationResponse(savedTenant, registeredAdmin));
	}

	private StaffRegistrationResponse registerAdmin(TenantRegistrationRequest request, TenantDomain saved){
		StaffRegistrationRequest registrationRequest = new StaffRegistrationRequest(request.username(), request.password(), request.fullName(),
				request.description(), saved.id(), request.emails(), request.phones(), request.addresses(), saved.createdBy());
		return gateway.registerAdmin(registrationRequest);
	}

	private TenantDomain saveTenant(TenantRegistrationRequest request){
		FullNameDomain name = new FullNameDomain(request.fullName());
		DescriptionDomain description = new DescriptionDomain(request.description());
		TenantDomain tenant = TenantDomain.register(name, description);
		return gateway.save(tenant);
	}

}
