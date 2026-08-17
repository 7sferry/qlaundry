package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.HashedPasswordDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record StaffPasswordDomain(String id, String staffId, HashedPasswordDomain password,
                                  Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                                  String updatedBy){
	public StaffPasswordDomain{
		if(staffId == null || password == null){
			throw new IllegalArgumentException("Staff id and password must not be null");
		}
	}

	public static StaffPasswordDomain register(String staffId, HashedPasswordDomain password, String createdBy){
		Instant now = Instant.now();
		return new StaffPasswordDomain(null, staffId, password, null, false, now, createdBy, now, createdBy);
	}

	public String passwordValue(){
		return password.value();
	}
}
