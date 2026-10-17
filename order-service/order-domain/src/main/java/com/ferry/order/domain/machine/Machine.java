package com.ferry.order.domain.machine;

import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record Machine(String id, String tenantId, String name, MachineType type, Note description,
                      boolean active, Integer version, boolean deleted, Instant createdAt, String createdBy,
                      Instant updatedAt, String updatedBy){

	public Machine{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidOrderStateException("Tenant id must not be blank");
		}
		if(name == null || name.isBlank()){
			throw new InvalidOrderStateException("Machine name must not be blank");
		}
		if(type == null){
			throw new InvalidOrderStateException("Machine type must not be null");
		}
		name = name.trim();
	}

	public static Machine create(String tenantId, String name, MachineType type, Note description,
	                             String createdBy){
		Instant now = Instant.now();
		return new Machine(null, tenantId, name, type, description, true, null, false, now, createdBy,
				now, createdBy);
	}

	public Machine update(String name, MachineType type, Note description, boolean active,
	                      String updatedBy){
		return toBuilder()
				.name(name)
				.type(type)
				.description(description)
				.active(active)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public String descriptionValue(){
		return description == null ? null : description.value();
	}

}
