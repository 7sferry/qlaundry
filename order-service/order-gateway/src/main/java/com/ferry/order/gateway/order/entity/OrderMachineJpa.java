package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.machine.MachineType;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.gateway.machine.entity.MachineJpa;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
@Table(name = "order_machines",
		uniqueConstraints = @UniqueConstraint(name = "uq_order_machines_order_id_machine_id",
				columnNames = {"order_id", "machine_id"}),
		indexes = @Index(name = "idx_order_machines_machine_id", columnList = "machine_id"))
public class OrderMachineJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private OrderJpa order;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "order_id", insertable = false, updatable = false)
	private String orderId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private MachineJpa machine;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "machine_id", length = 50, insertable = false, updatable = false)
	private String machineId;
	@Column(nullable = false, length = 100)
	private String machineName;
	@Column(nullable = false)
	private short machineTypeId;
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

	public void setOrder(OrderJpa order){
		this.order = order;
		this.orderId = order.getId();
	}

	public void setMachine(MachineJpa machine){
		this.machine = machine;
		this.machineId = machine.getId();
	}

	public static OrderMachineJpa construct(String id, OrderMachine machine, OrderJpa order, MachineJpa machineRef){
		OrderMachineJpa entity = new OrderMachineJpa();
		entity.setId(id);
		entity.setOrder(order);
		entity.setMachine(machineRef);
		entity.setMachineName(machine.machineName());
		entity.setMachineTypeId(machine.machineType().getValue());
		entity.setCreatedBy(machine.createdBy());
		entity.setCreatedAt(machine.createdAt());
		entity.setUpdatedBy(machine.updatedBy());
		entity.setUpdatedAt(machine.updatedAt());
		entity.setDeleted(machine.deleted());
		entity.setVersion(machine.version());
		return entity;
	}

	public static OrderMachine construct(OrderMachineJpa saved){
		return new OrderMachine(saved.getId(), saved.getOrderId(), saved.getMachineId(), saved.getMachineName(),
				MachineType.fromValue(saved.getMachineTypeId()).orElseThrow(), saved.getVersion(), saved.isDeleted(),
				saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
