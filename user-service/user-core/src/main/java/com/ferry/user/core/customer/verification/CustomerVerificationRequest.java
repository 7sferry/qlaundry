package com.ferry.user.core.customer.verification;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerVerificationRequest(@NotBlank String customerId,
                                          @NotBlank String tenantId) implements UserValidation{
}
