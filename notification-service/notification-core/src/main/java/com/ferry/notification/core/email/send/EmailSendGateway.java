package com.ferry.notification.core.email.send;

import com.ferry.notification.domain.EmailNotificationDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailSendGateway{
	void send(EmailNotificationDomain notification);
}
