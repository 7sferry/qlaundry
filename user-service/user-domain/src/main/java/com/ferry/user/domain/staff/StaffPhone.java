package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffPhone(
	String id,
	String staffId,
	Phone phone,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public StaffPhone{
		if(staffId == null || phone == null){
			throw new InvalidUserStateException("User id and phone must not be null");
		}
	}

	public static StaffPhone register(String userId, Phone phone, String createdBy){
		Instant now = Instant.now();
		return new StaffPhone(null, userId, phone, null, false, now, createdBy, now, createdBy);
	}
}
