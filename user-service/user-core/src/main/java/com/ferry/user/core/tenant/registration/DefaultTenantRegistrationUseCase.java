package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.core.tools.UserEmailPublisher;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.registration.TurnstileVerificationException;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HexFormat;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultTenantRegistrationUseCase implements TenantRegistrationUseCase{
	private static final int CONFIRMATION_TOKEN_BYTES = 32;

	private final TenantRegistrationGateway gateway;
	private final UserEmailPublisher emailPublisher;
	private final VerificationGateway verificationGateway;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(TenantRegistrationRequest request, TenantRegistrationPresenter presenter){
		request.validate();
		verifyCaptcha(request);
		try{
			Tenant savedTenant = saveTenant(request);
			StaffRegistrationResponse registeredAdmin = registerAdmin(request, savedTenant);
			triggerRegistrationEmail(request, savedTenant, registeredAdmin);
			presenter.present(new TenantRegistrationResponse(savedTenant, registeredAdmin));
		} catch (InvalidUsernameException e){
			log.warn("Failed to register tenant", e);
			Tenant fakeTenant = Tenant.fake(new FullName(request.tenantName()), new Username(request.username()));
			Staff fakeStaff = Staff.fake(new FullName(request.fullName()), new Username(request.username()));
			presenter.present(new TenantRegistrationResponse(fakeTenant, new StaffRegistrationResponse(fakeStaff)));
		}
	}

	private void verifyCaptcha(TenantRegistrationRequest request){
		if(!verificationGateway.verify(request.captchaToken())){
			throw new TurnstileVerificationException("Failed captcha verification");
		}
	}

	private StaffRegistrationResponse registerAdmin(TenantRegistrationRequest request, Tenant saved){
		StaffRegistrationRequest registrationRequest = new StaffRegistrationRequest(request.username(), request.password(), request.fullName(),
				request.description() != null ? request.description() : "Super Admin", StaffRole.SUPER_STAFF, request.emails(), request.phones(), request.addresses());
		return gateway.registerAdmin(registrationRequest, saved);
	}

	private Tenant saveTenant(TenantRegistrationRequest request){
		Username username = new Username(request.username());
		if(gateway.existsByUsername(username)){
			throw new InvalidUsernameException("Username already exists");
		}
		FullName name = new FullName(request.tenantName());
		Description description = new Description(request.description());
		Tenant tenant = Tenant.register(username, name, description);
		return gateway.save(tenant);
	}

	private void triggerRegistrationEmail(TenantRegistrationRequest request, Tenant tenant, StaffRegistrationResponse registeredAdmin){
		if(request.emails() == null || request.emails().isEmpty()){
			return;
		}
		Staff admin = registeredAdmin.user();
		String confirmationToken = generateConfirmationToken();
		cacheManager.set(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenant.id(), confirmationToken,
				TenantConfirmationConstant.CONFIRM_TOKEN_DURATION);
		TenantRegistrationEmailMessage message = new TenantRegistrationEmailMessage(admin.fullNameValue(),
				admin.usernameValue(), tenant.id(), tenant.fullNameValue(),
				tenant.descriptionValue(), tenant.createdAt(), confirmationToken);
		EmailTriggerConfig config = new EmailTriggerConfig(message, tenant.createdBy(),
				EmailTriggerType.TENANT_REGISTRATION, new Email(request.emails().getFirst()));
		EmailTrigger trigger = emailPublisher.save(config);
		emailPublisher.publish(trigger);
	}

	private String generateConfirmationToken(){
		byte[] tokenBytes = new byte[CONFIRMATION_TOKEN_BYTES];
		PasswordConstant.getRandom().nextBytes(tokenBytes);
		return HexFormat.of().formatHex(tokenBytes);
	}

}
