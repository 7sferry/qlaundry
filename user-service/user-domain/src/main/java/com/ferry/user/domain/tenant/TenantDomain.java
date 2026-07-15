package com.ferry.user.domain.tenant;

import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantDomain(String id, FullNameDomain fullName, DescriptionDomain description, Integer version, boolean deleted,
                           Instant createdAt, String createdBy, Instant updatedAt, String updatedBy){

	public TenantDomain {
		if(fullName == null){
			throw new IllegalArgumentException("Name must not be null");
		}
	}

	public static TenantDomain register(FullNameDomain name, DescriptionDomain description){
		return new TenantDomain(null, name, description, null, false, Instant.now(), null,
				Instant.now(), null);
	}

	public String descriptionValue(){
		return description.value();
	}

	public String fullNameValue(){
		return fullName.value();
	}

}
