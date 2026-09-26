package com.ferry.notification.core.email.send;

import com.ferry.notification.domain.Content;
import com.ferry.notification.domain.EmailNotification;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailSendGateway{
	void send(EmailNotification notification, Content content);
}
