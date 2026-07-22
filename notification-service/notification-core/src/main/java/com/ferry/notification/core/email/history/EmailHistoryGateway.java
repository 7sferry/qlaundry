package com.ferry.notification.core.email.history;

import com.ferry.notification.domain.EmailNotificationDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailHistoryGateway{
	EmailNotificationDomain save(EmailNotificationDomain notification);
}
