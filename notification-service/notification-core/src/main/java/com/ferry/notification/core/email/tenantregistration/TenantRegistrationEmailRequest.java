package com.ferry.notification.core.email.tenantregistration;

import com.ferry.notification.core.tools.NotificationValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantRegistrationEmailRequest(@NotBlank String triggerId, @NotBlank String recipient,
                                             @NotBlank String staffFullName, @NotBlank String staffUsername,
                                             @NotBlank String tenantId, @NotBlank String tenantName,
                                             String tenantDescription, @NotNull Instant registeredAt,
                                             @NotBlank String confirmationToken) implements NotificationValidation{
}
