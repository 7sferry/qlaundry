package com.ferry.order.core.machine.create;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineCreateGateway{
	boolean existsByName(String name, TenantId tenantId);

	Machine save(Machine machine);
}
