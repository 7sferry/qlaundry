package com.ferry.user.core.staff.update;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffUpdateRequest(@NotBlank String fullName, String description, String currentPassword, String newPassword,
                                 List<String> emails, List<String> phones,
                                 List<String> addresses) implements UserValidation{
}
