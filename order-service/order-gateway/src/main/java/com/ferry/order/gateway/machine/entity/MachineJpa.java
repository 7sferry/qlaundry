package com.ferry.order.gateway.machine.entity;

import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "machines", indexes = @Index(name = "idx_machines_tenant_id", columnList = "tenant_id"))
public class MachineJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, length = 100)
	private String name;
	@Column(nullable = false)
	private short typeId;
	@Column
	private Double capacityKg;
	@Column
	private String description;
	@Column(nullable = false)
	private boolean active;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	public static MachineJpa construct(String id, Machine machine){
		MachineJpa entity = new MachineJpa();
		entity.setId(id);
		entity.setTenantId(machine.tenantId());
		entity.setName(machine.name());
		entity.setTypeId(machine.type().getValue());
		entity.setCapacityKg(machine.capacityKg());
		entity.setDescription(machine.descriptionValue());
		entity.setActive(machine.active());
		entity.setCreatedBy(machine.createdBy());
		entity.setCreatedAt(machine.createdAt());
		entity.setUpdatedBy(machine.updatedBy());
		entity.setUpdatedAt(machine.updatedAt());
		entity.setDeleted(machine.deleted());
		entity.setVersion(machine.version());
		return entity;
	}

	public static Machine construct(MachineJpa saved){
		return new Machine(saved.getId(), saved.getTenantId(), saved.getName(),
				MachineType.fromValue(saved.getTypeId()).orElseThrow(), saved.getCapacityKg(),
				new Note(saved.getDescription()), saved.isActive(), saved.getVersion(), saved.isDeleted(),
				saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
