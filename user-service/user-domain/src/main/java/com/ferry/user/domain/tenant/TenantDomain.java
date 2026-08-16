package com.ferry.user.domain.tenant;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.UsernameDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantDomain(String id, UsernameDomain username, FullNameDomain fullName, DescriptionDomain description,
                           TenantStatus status, Integer version, boolean deleted, Instant createdAt, String createdBy,
                           Instant updatedAt, String updatedBy){

	public TenantDomain {
		if(username == null || fullName == null){
			throw new IllegalArgumentException("Username and name must not be null");
		}
	}

	public static TenantDomain register(UsernameDomain username, FullNameDomain name, DescriptionDomain description){
		Instant now = Instant.now();
		return new TenantDomain(null, username, name, description, TenantStatus.PENDING, null, false, now, null, now, null);
	}

	public String usernameValue(){
		return username.value();
	}

	public String descriptionValue(){
		return description.value();
	}

	public String fullNameValue(){
		return fullName.value();
	}

	public TenantDomain activate(){
		return new TenantDomain(id, username, fullName, description, TenantStatus.ACTIVE, version, deleted, createdAt,
				createdBy, Instant.now(), updatedBy);
	}

}
