package com.ferry.notification.core.email.forgottenpassword;

import com.ferry.notification.core.tools.NotificationValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record ForgottenPasswordEmailRequest(@NotBlank String triggerId, @NotBlank String recipient,
                                            @NotBlank String username,
                                            @NotBlank String otp) implements NotificationValidation{
}
