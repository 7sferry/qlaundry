package com.ferry.order.gateway.machine;

import com.ferry.order.core.machine.create.MachineCreateGateway;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.machine.entity.MachineJpa;
import com.ferry.order.gateway.machine.repository.MachineJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaMachineCreateGateway implements MachineCreateGateway{
	private final MachineJpaRepository machineJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public boolean existsByName(String name, TenantId tenantId){
		return machineJpaRepository.existsByNameIgnoreCaseAndTenantIdAndDeletedIsFalse(name, tenantId.value());
	}

	@Override
	public Machine save(Machine machine){
		String id = idGenerator.generateId();
		MachineJpa saved = machineJpaRepository.save(MachineJpa.construct(id, machine));
		return MachineJpa.construct(saved);
	}

}
