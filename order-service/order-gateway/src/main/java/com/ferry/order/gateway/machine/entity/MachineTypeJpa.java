package com.ferry.order.gateway.machine.entity;

import com.ferry.order.gateway.order.entity.OrderMachineJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "machine_types")
public class MachineTypeJpa{
	@Id
	private short id;

	@Column(nullable = false, length = 25)
	private String name;

	@JoinColumn(name = "typeId")
	@OneToMany(fetch = FetchType.LAZY)
	private Set<MachineJpa> machines;

	@JoinColumn(name = "machineTypeId")
	@OneToMany(fetch = FetchType.LAZY)
	private Set<OrderMachineJpa> orderMachines;
}
