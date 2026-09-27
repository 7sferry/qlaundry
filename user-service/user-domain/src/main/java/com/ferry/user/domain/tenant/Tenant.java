package com.ferry.user.domain.tenant;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record Tenant(
	String id,
	Username username,
	FullName fullName,
	Description description,
	ZoneId timeZone,
	TenantStatus status,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");

	public Tenant {
		if(username == null || fullName == null){
			throw new InvalidUserStateException("Username and name must not be null");
		}
		timeZone = timeZone == null ? DEFAULT_TIME_ZONE : timeZone;
	}

	public static Tenant fake(FullName name, Username username){
		return new Tenant(null, username, name, null, null, TenantStatus.ACTIVE, null, false, null, null, null, null);
	}

	public static Tenant register(Username username, FullName name, Description description, ZoneId timeZone){
		Instant now = Instant.now();
		return new Tenant(null, username, name, description, timeZone, TenantStatus.PENDING, null, false, now, null,
				now, null);
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

	public Tenant activate(){
		return new Tenant(id, username, fullName, description, timeZone, TenantStatus.ACTIVE, version, deleted,
				createdAt, createdBy, Instant.now(), updatedBy);
	}

}
