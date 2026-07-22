package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.domain.notification.EmailTriggerDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserEmailPublisher{
	EmailTriggerDomain save(EmailTriggerConfig config);
	void publish(EmailTriggerDomain trigger);
}
