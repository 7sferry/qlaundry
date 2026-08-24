package com.ferry.user.core.customer.update;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerUpdateRequest(@NotBlank String customerId, @NotBlank String fullName, @NotBlank String phone,
                                    String email, String address, String notes) implements UserValidation{
}
