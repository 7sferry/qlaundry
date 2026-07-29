package com.ferry.user.core.staff.submitotp;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffSubmitOtpRequest(@NotBlank String username, @NotBlank String otp) implements UserValidation{
}
