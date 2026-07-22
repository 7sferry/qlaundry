package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.tenant.TenantDomain;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantRegistrationUseCase implements TenantRegistrationUseCase{
	private final TenantRegistrationGateway gateway;
	private final TenantRegistrationEmailGateway emailGateway;

	@Override
	public void execute(TenantRegistrationRequest request, TenantRegistrationPresenter presenter){
		TenantDomain savedTenant = saveTenant(request);
		StaffRegistrationResponse registeredAdmin = registerAdmin(request, savedTenant);
		triggerRegistrationEmail(request, savedTenant, registeredAdmin);
		presenter.present(new TenantRegistrationResponse(savedTenant, registeredAdmin));
	}

	private StaffRegistrationResponse registerAdmin(TenantRegistrationRequest request, TenantDomain saved){
		StaffRegistrationRequest registrationRequest = new StaffRegistrationRequest(request.username(), request.password(), request.fullName(),
				request.description() != null ? request.description() : "Super Admin", saved.id(), request.emails(), request.phones(), request.addresses());
		return gateway.registerAdmin(registrationRequest, saved.createdBy());
	}

	private TenantDomain saveTenant(TenantRegistrationRequest request){
		FullNameDomain name = new FullNameDomain(request.tenantName());
		DescriptionDomain description = new DescriptionDomain(request.description());
		TenantDomain tenant = TenantDomain.register(name, description);
		return gateway.save(tenant);
	}

	private void triggerRegistrationEmail(TenantRegistrationRequest request, TenantDomain tenant, StaffRegistrationResponse registeredAdmin){
		if(request.emails() == null || request.emails().isEmpty()){
			return;
		}
		StaffDomain admin = registeredAdmin.user();
		TenantRegistrationEmailMessage message = new TenantRegistrationEmailMessage(request.emails().getFirst(),
				admin.fullNameValue(), admin.usernameValue(), tenant.id(), tenant.fullNameValue(),
				tenant.descriptionValue(), tenant.createdAt());
		EmailTriggerDomain trigger = emailGateway.save(message, tenant.createdBy());
		emailGateway.publish(trigger);
	}

}
