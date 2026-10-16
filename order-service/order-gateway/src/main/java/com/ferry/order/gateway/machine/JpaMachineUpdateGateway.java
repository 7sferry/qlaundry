package com.ferry.order.gateway.machine;

import com.ferry.order.core.machine.update.MachineUpdateGateway;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.machine.entity.MachineJpa;
import com.ferry.order.gateway.machine.repository.MachineJpaRepository;
import com.ferry.order.gateway.order.repository.OrderMachineJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaMachineUpdateGateway implements MachineUpdateGateway{
	private final MachineJpaRepository machineJpaRepository;
	private final OrderMachineJpaRepository orderMachineJpaRepository;

	@Override
	public Optional<Machine> lockById(MachineId machineId, TenantId tenantId){
		return machineJpaRepository.lockByIdAndTenantId(machineId.value(), tenantId.value())
				.map(MachineJpa::construct);
	}

	@Override
	public boolean existsByNameExcept(String name, TenantId tenantId, MachineId machineId){
		return machineJpaRepository.existsByNameIgnoreCaseAndTenantIdAndIdNotAndDeletedIsFalse(name,
				tenantId.value(), machineId.value());
	}

	@Override
	public Optional<String> findCurrentOrderNumber(MachineId machineId){
		return orderMachineJpaRepository.findOccupancies(List.of(machineId.value()), OrderStatus.IN_PROGRESS.getValue())
				.stream()
				.findFirst()
				.map(MachineOccupancy::orderNumber);
	}

	@Override
	public Machine save(Machine machine){
		MachineJpa saved = machineJpaRepository.saveAndFlush(MachineJpa.construct(machine.id(), machine));
		return MachineJpa.construct(saved);
	}

}
