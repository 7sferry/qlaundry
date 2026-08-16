package com.ferry.notification.core.email.history;

import com.ferry.notification.domain.EmailNotificationDomain;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailHistoryGateway{
	EmailNotificationDomain save(EmailNotificationDomain notification);

	Optional<EmailNotificationDomain> findByReferenceId(String referenceId);
}
