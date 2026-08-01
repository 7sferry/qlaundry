package com.ferry.user.domain.tenant;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantDomain(String id, FullNameDomain fullName, DescriptionDomain description, TenantStatus status,
                           Integer version, boolean deleted, Instant createdAt, String createdBy, Instant updatedAt,
                           String updatedBy){

	public TenantDomain {
		if(fullName == null){
			throw new IllegalArgumentException("Name must not be null");
		}
	}

	public static TenantDomain register(FullNameDomain name, DescriptionDomain description){
		Instant now = Instant.now();
		return new TenantDomain(null, name, description, TenantStatus.PENDING, null, false, now, null, now, null);
	}

	public String descriptionValue(){
		return description.value();
	}

	public String fullNameValue(){
		return fullName.value();
	}

	public TenantDomain activate(){
		return new TenantDomain(id, fullName, description, TenantStatus.ACTIVE, version, deleted, createdAt,
				createdBy, Instant.now(), updatedBy);
	}

}
