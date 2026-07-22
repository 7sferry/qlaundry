package com.ferry.user.core.tenant.registration;

import com.ferry.user.domain.notification.EmailTriggerDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantRegistrationEmailGateway{
	EmailTriggerDomain save(TenantRegistrationEmailMessage message, String userId);
	void publish(EmailTriggerDomain trigger);
}
