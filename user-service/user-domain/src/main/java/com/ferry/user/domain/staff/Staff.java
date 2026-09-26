package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record Staff(
	String id,
	Username username,
	FullName fullName,
	Description description,
	String tenantId,
	StaffRole role,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public Staff{
		if(username == null || fullName == null){
			throw new InvalidUserStateException("Username and full fullName must not be null");
		}
	}

	public static Staff fake(FullName name, Username username){
		return new Staff(null, username, name, null, null, StaffRole.STAFF, null, false, null, null, null, null);
	}

	public static Staff register(Username username, FullName fullName,
	                                   Description note, String tenantId, StaffRole role, String createdBy){
		Instant now = Instant.now();
		return new Staff(null, username, fullName, note, tenantId, role, null,false, now, createdBy, now, createdBy);
	}

	public Staff update(FullName fullName, Description note, String updatedBy){
		return new Staff(id, username, fullName, note, tenantId, role, version, deleted, createdAt, createdBy, Instant.now(), updatedBy);
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

	public Staff markDeleted(String updatedBy){
		return toBuilder().deleted(true).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}
}
