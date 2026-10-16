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
		entity.id = id;
		entity.tenantId = machine.tenantId();
		entity.name = machine.name();
		entity.typeId = machine.type().getValue();
		entity.capacityKg = machine.capacityKg();
		entity.description = machine.descriptionValue();
		entity.active = machine.active();
		entity.createdBy = machine.createdBy();
		entity.createdAt = machine.createdAt();
		entity.updatedBy = machine.updatedBy();
		entity.updatedAt = machine.updatedAt();
		entity.deleted = machine.deleted();
		entity.version = machine.version();
		return entity;
	}

	public static Machine construct(MachineJpa saved){
		return new Machine(saved.id, saved.tenantId, saved.name, MachineType.fromValue(saved.typeId).orElseThrow(),
				saved.capacityKg, new Note(saved.description), saved.active, saved.version, saved.deleted,
				saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
