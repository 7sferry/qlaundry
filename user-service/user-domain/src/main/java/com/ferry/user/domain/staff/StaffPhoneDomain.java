package com.ferry.user.domain.staff;

import com.ferry.user.domain.PhoneDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffPhoneDomain(String id, String staffId, PhoneDomain phone,
                               Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                               String updatedBy){
	public StaffPhoneDomain{
		if(staffId == null || phone == null){
			throw new IllegalArgumentException("User id and phone must not be null");
		}
	}

	public static StaffPhoneDomain register(String userId, PhoneDomain phone, String createdBy){
		return new StaffPhoneDomain(null, userId, phone, null, false, Instant.now(), createdBy, Instant.now(), createdBy);
	}
}
