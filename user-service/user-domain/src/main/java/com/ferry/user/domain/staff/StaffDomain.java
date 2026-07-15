package com.ferry.user.domain.staff;

import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.HashedPasswordDomain;
import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.UsernameDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffDomain(String id, UsernameDomain username, HashedPasswordDomain password, FullNameDomain fullName,
                          DescriptionDomain description, String tenantId,
                          Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                          String updatedBy){
	public StaffDomain{
		if(username == null || password == null || fullName == null){
			throw new IllegalArgumentException("Username, password, and full fullName must not be null");
		}
	}

	public static StaffDomain register(UsernameDomain username, HashedPasswordDomain password, FullNameDomain fullName,
	                                   DescriptionDomain note, String tenantId, String createdBy){
		return new StaffDomain(null, username, password, fullName, note, tenantId, null, false, Instant.now(), createdBy, Instant.now(), createdBy);
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

	public String passwordValue(){
		return password.value();
	}
}
