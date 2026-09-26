package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record StaffPassword(
	String id,
	String staffId,
	HashedPassword password,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public StaffPassword{
		if(staffId == null || password == null){
			throw new InvalidUserStateException("Staff id and password must not be null");
		}
	}

	public static StaffPassword register(String staffId, HashedPassword password, String createdBy){
		Instant now = Instant.now();
		return new StaffPassword(null, staffId, password, null, false, now, createdBy, now, createdBy);
	}

	public String passwordValue(){
		return password.value();
	}
}
