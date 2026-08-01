package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

public record TenantResendConfirmationRequest(@NotBlank String tenantId) implements UserValidation{
}
