package com.ferry.user.core.customer.registration;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerRegistrationRequest(@NotBlank String fullName, @NotBlank String phone, String email,
                                          String address, String notes) implements UserValidation{
}
