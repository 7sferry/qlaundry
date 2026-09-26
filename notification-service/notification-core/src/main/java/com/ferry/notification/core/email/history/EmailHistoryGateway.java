package com.ferry.notification.core.email.history;

import com.ferry.notification.domain.EmailNotification;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailHistoryGateway{
	EmailNotification save(EmailNotification notification);

	Optional<EmailNotification> findByReferenceId(String referenceId);
}
