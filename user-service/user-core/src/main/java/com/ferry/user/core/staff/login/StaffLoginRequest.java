package com.ferry.user.core.staff.login;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffLoginRequest(
	@NotBlank String username,
	String password,
	String otp,
	String captchaToken) implements UserValidation{
}
