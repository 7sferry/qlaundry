package com.ferry.user.core.customer.detail;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerDetailRequest(@NotBlank String customerId) implements UserValidation{
}
