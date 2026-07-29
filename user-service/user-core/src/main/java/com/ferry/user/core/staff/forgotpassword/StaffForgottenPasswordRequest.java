package com.ferry.user.core.staff.forgotpassword;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffForgottenPasswordRequest(@NotBlank String username) implements UserValidation{
}
