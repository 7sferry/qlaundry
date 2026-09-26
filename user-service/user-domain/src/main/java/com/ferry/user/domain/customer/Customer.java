package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record Customer(
	String id,
	String tenantId,
	FullName fullName,
	Description notes,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public Customer{
		if(fullName == null){
			throw new InvalidUserStateException("Full name must not be null");
		}
	}

	public static Customer register(String tenantId, FullName fullName, Description notes,
	                                      String createdBy){
		Instant now = Instant.now();
		return new Customer(null, tenantId, fullName, notes, null, false, now, createdBy, now, createdBy);
	}

	public Customer update(FullName fullName, Description notes, String updatedBy){
		return toBuilder()
				.fullName(fullName)
				.notes(notes)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public Customer markDeleted(String updatedBy){
		return toBuilder().deleted(true).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public String fullNameValue(){
		return fullName.value();
	}

	public String notesValue(){
		return notes == null ? null : notes.value();
	}

}
