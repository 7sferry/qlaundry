package com.ferry.user.core.tools;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PasswordSubject(
	String username,
	String fullName,
	String tenantName,
	String tenantUsername,
	List<String> emails,
	List<String> phones){
	public PasswordSubject{
		emails = emails == null ? List.of() : emails;
		phones = phones == null ? List.of() : phones;
	}
}
