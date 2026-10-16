package com.ferry.order.core.order.machines;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderMachinesGateway{
	Optional<Order> findById(OrderId orderId, TenantId tenantId);

	List<OrderMachine> findMachinesByOrderId(OrderId orderId);

	List<Machine> lockMachines(Set<MachineId> machineIds, TenantId tenantId);

	List<MachineOccupancy> findOccupancies(Set<MachineId> machineIds, OrderId excludedOrderId);

	List<OrderMachine> saveMachines(List<OrderMachine> machines);
}
