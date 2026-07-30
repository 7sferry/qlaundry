package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantRegistrationRequest(@NotBlank String fullName, @NotBlank String tenantName, String description,
                                        @NotBlank String username, @NotBlank String password,
                                        @NotEmpty List<String> emails, List<String> phones, List<String> addresses,
                                        @NotBlank String captchaToken) implements UserValidation{
}
