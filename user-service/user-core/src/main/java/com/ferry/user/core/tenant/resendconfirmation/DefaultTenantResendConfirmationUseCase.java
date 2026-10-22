package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tenant.registration.TenantRegistrationEmailMessage;
import com.ferry.user.core.tools.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.core.tools.ResponseTimeFloor;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.resendconfirmation.FailedToResendConfirmationException;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HexFormat;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultTenantResendConfirmationUseCase implements TenantResendConfirmationUseCase{
	private static final int CONFIRMATION_TOKEN_BYTES = 32;
	private static final Duration MIN_RESPONSE_TIME = Duration.ofMillis(300);

	private final TenantResendConfirmationGateway gateway;
	private final UserEmailPublisher emailPublisher;
	private final UserCacheManager cacheManager;
	private final AuthAuditLog auditLog;
	private final ResponseTimeFloor responseTimeFloor;

	@Override
	public void execute(TenantResendConfirmationRequest request, TenantResendConfirmationPresenter presenter){
		long startedAt = System.nanoTime();
		String subject = request.tenantId();
		try{
			request.validate();
			TenantId tenantId = new TenantId(request.tenantId());
			Tenant tenant = gateway.findById(tenantId)
					.orElseThrow(() -> new FailedToResendConfirmationException("Tenant not found"));
			if(tenant.status() == TenantStatus.ACTIVE){
				throw new FailedToResendConfirmationException("Tenant already confirmed");
			}
			TenantAdminContactProjection admin = gateway.findAdminContact(tenantId)
					.orElseThrow(() -> new FailedToResendConfirmationException("Tenant not found"));
			resendConfirmationEmail(tenant, admin);
			auditLog.record(AuthAction.TENANT_RESEND_CONFIRMATION, AuthResult.SUCCESS, null, subject);
			presenter.present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
			responseTimeFloor.enforce(startedAt, MIN_RESPONSE_TIME);
		} catch (Exception e){
			log.error("Failed to process resend confirmation request", e);
			auditLog.record(AuthAction.TENANT_RESEND_CONFIRMATION, AuthResult.FAILURE, null, subject);
			presenter.present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
			responseTimeFloor.enforce(startedAt, MIN_RESPONSE_TIME);
		}
	}

	private void resendConfirmationEmail(Tenant tenant, TenantAdminContactProjection admin){
		String confirmationToken = generateConfirmationToken();
		cacheManager.set(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenant.id(), confirmationToken,
				TenantConfirmationConstant.CONFIRM_TOKEN_DURATION);
		TenantRegistrationEmailMessage message = new TenantRegistrationEmailMessage(admin.staffFullName(),
				admin.staffUsername(), tenant.id(), tenant.fullNameValue(),
				tenant.descriptionValue(), tenant.createdAt(), confirmationToken);
		EmailTriggerConfig config = new EmailTriggerConfig(message, tenant.createdBy(), EmailTriggerType.TENANT_REGISTRATION,
				new Email(admin.email()));
		EmailTrigger trigger = emailPublisher.save(config);
		emailPublisher.publish(trigger);
	}

	private String generateConfirmationToken(){
		byte[] tokenBytes = new byte[CONFIRMATION_TOKEN_BYTES];
		PasswordConstant.getRandom().nextBytes(tokenBytes);
		return HexFormat.of().formatHex(tokenBytes);
	}

}
