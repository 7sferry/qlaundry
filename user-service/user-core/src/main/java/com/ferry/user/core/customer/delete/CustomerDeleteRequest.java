package com.ferry.user.core.customer.delete;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerDeleteRequest(@NotBlank String customerId) implements UserValidation{
}
