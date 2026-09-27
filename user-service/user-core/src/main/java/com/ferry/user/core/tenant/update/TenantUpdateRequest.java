package com.ferry.user.core.tenant.update;

import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

public record TenantUpdateRequest(
	@NotBlank String tenantName,
	String description,
	@NotNull ZoneId timeZone) implements UserValidation{
}
