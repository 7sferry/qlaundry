package com.ferry.order.core.order.process;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.order.OrderStaff;
import com.ferry.order.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderProcessGateway{
	Optional<Order> lockById(OrderId orderId, TenantId tenantId);

	Order save(Order order);

	List<OrderItem> findItemsByOrderId(OrderId orderId);

	List<OrderPromotion> findPromotionsByOrderId(OrderId orderId);

	List<Machine> lockMachines(Set<MachineId> machineIds, TenantId tenantId);

	List<MachineOccupancy> findOccupancies(Set<MachineId> machineIds, OrderId excludedOrderId);

	List<OrderMachine> saveMachines(List<OrderMachine> machines);

	List<OrderStaff> saveStaffs(List<OrderStaff> staffs);
}
