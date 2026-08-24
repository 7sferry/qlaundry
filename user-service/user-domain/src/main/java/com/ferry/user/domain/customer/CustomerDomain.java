package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record CustomerDomain(String id, String tenantId, FullNameDomain fullName, DescriptionDomain notes,
                             Integer version, boolean deleted, Instant createdAt, String createdBy,
                             Instant updatedAt, String updatedBy){
	public CustomerDomain{
		if(fullName == null){
			throw new IllegalArgumentException("Full name must not be null");
		}
	}

	public static CustomerDomain register(String tenantId, FullNameDomain fullName, DescriptionDomain notes,
	                                      String createdBy){
		Instant now = Instant.now();
		return new CustomerDomain(null, tenantId, fullName, notes, null, false, now, createdBy, now, createdBy);
	}

	public CustomerDomain update(FullNameDomain fullName, DescriptionDomain notes, String updatedBy){
		return toBuilder()
				.fullName(fullName)
				.notes(notes)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public CustomerDomain markDeleted(String updatedBy){
		return toBuilder().deleted(true).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public String fullNameValue(){
		return fullName.value();
	}

	public String notesValue(){
		return notes == null ? null : notes.value();
	}

}
