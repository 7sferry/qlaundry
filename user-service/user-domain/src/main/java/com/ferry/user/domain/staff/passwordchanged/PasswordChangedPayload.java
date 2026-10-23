package com.ferry.user.domain.staff.passwordchanged;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PasswordChangedPayload(
	String username,
	Instant changedAt){
}
