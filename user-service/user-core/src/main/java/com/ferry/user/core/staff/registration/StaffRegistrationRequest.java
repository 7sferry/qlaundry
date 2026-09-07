package com.ferry.user.core.staff.registration;

import com.ferry.user.core.tools.UserValidation;
import com.ferry.user.domain.staff.StaffRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffRegistrationRequest(@NotBlank @Pattern(regexp = "^[a-zA-Z0-9_]+$") String username,
                                       @NotBlank String password, @NotBlank String fullName,
                                       String description,
                                       @NotNull StaffRole role, List<String> emails, List<String> phones,
                                       List<String> addresses) implements UserValidation{
}
