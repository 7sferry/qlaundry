package com.ferry.user.domain.staff;

import com.ferry.user.domain.EmailDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffEmailDomain(String id, String staffId, EmailDomain email,
                               Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                               String updatedBy){
	public StaffEmailDomain{
		if(staffId == null || email == null){
			throw new IllegalArgumentException("User id and email must not be null");
		}
	}

	public static StaffEmailDomain register(String userId, EmailDomain email, String createdBy){
		return new StaffEmailDomain(null, userId, email, null, false, Instant.now(), createdBy, Instant.now(), createdBy);
	}
}
