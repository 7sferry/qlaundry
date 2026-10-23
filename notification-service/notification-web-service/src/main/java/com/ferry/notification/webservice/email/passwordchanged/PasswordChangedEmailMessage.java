package com.ferry.notification.webservice.email.passwordchanged;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PasswordChangedEmailMessage(
	String username,
	Instant changedAt){
}
