package com.ferry.user.core.staff.detail;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffDetailRequest(@NotBlank String username) implements UserValidation{
}
