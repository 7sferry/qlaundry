package com.ferry.notification.core.email.passwordchanged;

import com.ferry.notification.core.tools.NotificationValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PasswordChangedEmailRequest(
	@NotBlank String triggerId,
	@NotBlank String recipient,
	@NotBlank String username,
	@NotNull Instant changedAt) implements NotificationValidation{
}
