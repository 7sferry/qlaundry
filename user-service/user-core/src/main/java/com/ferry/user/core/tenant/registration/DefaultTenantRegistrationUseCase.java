package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.registration.TurnstileVerificationException;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantDomain;
import lombok.RequiredArgsConstructor;

import java.util.HexFormat;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantRegistrationUseCase implements TenantRegistrationUseCase{
	private static final int CONFIRMATION_TOKEN_BYTES = 32;

	private final TenantRegistrationGateway gateway;
	private final UserEmailPublisher emailPublisher;
	private final TurnstileVerificationGateway turnstileVerificationGateway;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(TenantRegistrationRequest request, TenantRegistrationPresenter presenter){
		request.validate();
		verifyCaptcha(request);
		TenantDomain savedTenant = saveTenant(request);
		StaffRegistrationResponse registeredAdmin = registerAdmin(request, savedTenant);
		triggerRegistrationEmail(request, savedTenant, registeredAdmin);
		presenter.present(new TenantRegistrationResponse(savedTenant, registeredAdmin));
	}

	private void verifyCaptcha(TenantRegistrationRequest request){
		if(!turnstileVerificationGateway.verify(request.captchaToken())){
			throw new TurnstileVerificationException("Failed captcha verification");
		}
	}

	private StaffRegistrationResponse registerAdmin(TenantRegistrationRequest request, TenantDomain saved){
		StaffRegistrationRequest registrationRequest = new StaffRegistrationRequest(request.username(), request.password(), request.fullName(),
				request.description() != null ? request.description() : "Super Admin", StaffRole.SUPER_STAFF, request.emails(), request.phones(), request.addresses());
		return gateway.registerAdmin(registrationRequest, saved);
	}

	private TenantDomain saveTenant(TenantRegistrationRequest request){
		UsernameDomain username = new UsernameDomain(request.username());
		if(gateway.existsByUsername(username)){
			throw new InvalidUsernameException("Username already exists");
		}
		FullNameDomain name = new FullNameDomain(request.tenantName());
		DescriptionDomain description = new DescriptionDomain(request.description());
		TenantDomain tenant = TenantDomain.register(username, name, description);
		return gateway.save(tenant);
	}

	private void triggerRegistrationEmail(TenantRegistrationRequest request, TenantDomain tenant, StaffRegistrationResponse registeredAdmin){
		if(request.emails() == null || request.emails().isEmpty()){
			return;
		}
		StaffDomain admin = registeredAdmin.user();
		String confirmationToken = generateConfirmationToken();
		cacheManager.set(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenant.id(), confirmationToken,
				TenantConfirmationConstant.CONFIRM_TOKEN_DURATION);
		TenantRegistrationEmailMessage message = new TenantRegistrationEmailMessage(request.emails().getFirst(),
				admin.fullNameValue(), admin.usernameValue(), tenant.id(), tenant.fullNameValue(),
				tenant.descriptionValue(), tenant.createdAt(), confirmationToken);
		EmailTriggerConfig config = new EmailTriggerConfig(message, tenant.createdBy(),
				EmailTriggerType.TENANT_REGISTRATION, new EmailDomain(message.recipient()));
		EmailTriggerDomain trigger = emailPublisher.save(config);
		emailPublisher.publish(trigger);
	}

	private String generateConfirmationToken(){
		byte[] tokenBytes = new byte[CONFIRMATION_TOKEN_BYTES];
		PasswordConstant.getRandom().nextBytes(tokenBytes);
		return HexFormat.of().formatHex(tokenBytes);
	}

}
