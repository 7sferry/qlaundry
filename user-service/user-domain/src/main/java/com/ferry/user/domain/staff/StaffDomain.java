package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.UsernameDomain;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record StaffDomain(String id, UsernameDomain username, FullNameDomain fullName,
                          DescriptionDomain description, String tenantId, StaffRole role,
                          Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                          String updatedBy){
	public StaffDomain{
		if(username == null || fullName == null){
			throw new IllegalArgumentException("Username and full fullName must not be null");
		}
	}

	public static StaffDomain fake(FullNameDomain name){
		return new StaffDomain(null, new UsernameDomain(name.value()), name, null, null, StaffRole.STAFF, null, false, null, null, null, null);
	}

	public static StaffDomain register(UsernameDomain username, FullNameDomain fullName,
	                                   DescriptionDomain note, String tenantId, StaffRole role, String createdBy){
		Instant now = Instant.now();
		return new StaffDomain(null, username, fullName, note, tenantId, role, null,false, now, createdBy, now, createdBy);
	}

	public StaffDomain update(FullNameDomain fullName, DescriptionDomain note, String updatedBy){
		return new StaffDomain(id, username, fullName, note, tenantId, role, version, deleted, createdAt, createdBy, Instant.now(), updatedBy);
	}

	public String usernameValue(){
		return username.value();
	}

	public String fullNameValue(){
		return fullName.value();
	}

	public String descriptionValue(){
		return description == null ? null : description.value();
	}

	public StaffDomain markDeleted(String updatedBy){
		return toBuilder().deleted(true).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}
}
