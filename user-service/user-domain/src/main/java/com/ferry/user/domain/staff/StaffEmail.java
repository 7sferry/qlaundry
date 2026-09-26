package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffEmail(
	String id,
	String staffId,
	Email email,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public StaffEmail{
		if(staffId == null || email == null){
			throw new InvalidUserStateException("User id and email must not be null");
		}
	}

	public static StaffEmail register(String userId, Email email, String createdBy){
		Instant now = Instant.now();
		return new StaffEmail(null, userId, email, null, false, now, createdBy, now, createdBy);
	}
}
