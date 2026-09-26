package com.ferry.user.core.tools;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.domain.notification.EmailTrigger;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserEmailPublisher{
	EmailTrigger save(EmailTriggerConfig config);
	void publish(EmailTrigger trigger);
}
