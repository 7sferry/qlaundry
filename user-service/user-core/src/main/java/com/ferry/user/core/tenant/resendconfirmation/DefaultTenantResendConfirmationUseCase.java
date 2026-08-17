package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tenant.registration.TenantRegistrationEmailMessage;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.resendconfirmation.FailedToResendConfirmationException;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantResendConfirmationUseCase implements TenantResendConfirmationUseCase{
	private static final int CONFIRMATION_TOKEN_BYTES = 32;
	private static final long MIN_RESPONSE_MILLIS = 300;
	private static final long MAX_TIMEOUT_RANGE = 100;
	private static final long MILLIS_IN_NANO = 1_000_000L;

	private final TenantResendConfirmationGateway gateway;
	private final UserEmailPublisher emailPublisher;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(TenantResendConfirmationRequest request, TenantResendConfirmationPresenter presenter){
		long startedAt = System.nanoTime();
		try{
			request.validate();
			TenantIdDomain tenantId = new TenantIdDomain(request.tenantId());
			TenantDomain tenant = gateway.findById(tenantId)
					.orElseThrow(() -> new FailedToResendConfirmationException("Tenant not found"));
			if(tenant.status() == TenantStatus.ACTIVE){
				throw new FailedToResendConfirmationException("Tenant already confirmed");
			}
			TenantAdminContactProjection admin = gateway.findAdminContact(tenantId)
					.orElseThrow(() -> new FailedToResendConfirmationException("Tenant not found"));
			resendConfirmationEmail(tenant, admin);
			presenter.present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
			awaitMinimumResponseTime(startedAt);
		} catch (FailedToResendConfirmationException e){
			awaitMinimumResponseTime(startedAt);
			throw e;
		} catch (Exception e){
			awaitMinimumResponseTime(startedAt);
			throw new FailedToResendConfirmationException(e);
		}
	}

	private void resendConfirmationEmail(TenantDomain tenant, TenantAdminContactProjection admin){
		String confirmationToken = generateConfirmationToken();
		cacheManager.set(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenant.id(), confirmationToken,
				TenantConfirmationConstant.CONFIRM_TOKEN_DURATION);
		TenantRegistrationEmailMessage message = new TenantRegistrationEmailMessage(admin.email(),
				admin.staffFullName(), admin.staffUsername(), tenant.id(), tenant.fullNameValue(),
				tenant.descriptionValue(), tenant.createdAt(), confirmationToken);
		EmailTriggerConfig config = new EmailTriggerConfig(message, tenant.createdBy(), EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(message.recipient()));
		EmailTriggerDomain trigger = emailPublisher.save(config);
		emailPublisher.publish(trigger);
	}

	private String generateConfirmationToken(){
		byte[] tokenBytes = new byte[CONFIRMATION_TOKEN_BYTES];
		PasswordConstant.getRandom().nextBytes(tokenBytes);
		return HexFormat.of().formatHex(tokenBytes);
	}

	@SneakyThrows
	private void awaitMinimumResponseTime(long startedAtNanos){
		long elapsedMillis = (System.nanoTime() - startedAtNanos) / MILLIS_IN_NANO;
		long remainingMillis = MIN_RESPONSE_MILLIS - elapsedMillis;
		if(remainingMillis > 0){
			long maxTimeout = remainingMillis + MAX_TIMEOUT_RANGE;
			long timeout = PasswordConstant.getRandom().nextLong(remainingMillis, maxTimeout);
			TimeUnit.MILLISECONDS.sleep(timeout);
		}
	}

}
