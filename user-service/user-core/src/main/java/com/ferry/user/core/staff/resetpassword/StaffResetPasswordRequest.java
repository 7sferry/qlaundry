package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffResetPasswordRequest(@NotBlank String username, @NotBlank String password,
                                        @NotBlank String resetToken) implements UserValidation{
}
