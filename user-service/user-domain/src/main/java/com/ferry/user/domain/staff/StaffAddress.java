package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffAddress(
	String id,
	String staffId,
	AddressLine addressLine,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public StaffAddress{
		if(staffId == null || addressLine == null){
			throw new InvalidUserStateException("User id and full addressLine must not be null");
		}
	}

	public static StaffAddress register(String userId, AddressLine fullAddress, String createdBy){
		Instant now = Instant.now();
		return new StaffAddress(null, userId, fullAddress, null, false, now, createdBy, now, createdBy);
	}
}
