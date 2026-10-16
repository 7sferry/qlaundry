package com.ferry.order.core.machine.update;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineUpdateGateway{
	Optional<Machine> lockById(MachineId machineId, TenantId tenantId);

	boolean existsByNameExcept(String name, TenantId tenantId, MachineId machineId);

	Optional<String> findCurrentOrderNumber(MachineId machineId);

	Machine save(Machine machine);
}
