package com.ferry.user.domain.staff;

import com.ferry.user.domain.AddressLineDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffAddressDomain(String id, String staffId, AddressLineDomain addressLine,
                                 Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                                 String updatedBy){
	public StaffAddressDomain{
		if(staffId == null || addressLine == null){
			throw new IllegalArgumentException("User id and full address must not be null");
		}
	}

	public static StaffAddressDomain register(String userId, AddressLineDomain fullAddress, String createdBy){
		Instant now = Instant.now();
		return new StaffAddressDomain(null, userId, fullAddress, null, false, now, createdBy, now, createdBy);
	}
}
