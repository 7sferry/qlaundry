package com.ferry.order.core.order.assignmachines;

import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.common.exception.MachineBusyException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderAssignMachinesUseCase implements OrderAssignMachinesUseCase{
	private final OrderAssignMachinesGateway gateway;

	@Override
	public void execute(OrderAssignMachinesRequest request, OrderAuthPrincipal principal,
	                    OrderAssignMachinesPresenter presenter){
		request.validate();
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Order order = gateway.lockById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		if(order.status() != OrderStatus.IN_PROGRESS){
			throw new InvalidOrderStatusException("Machines can only be changed while the order is in progress");
		}
		Set<MachineId> requested = machineIds(request.machineIds());
		Set<String> requestedValues = requested.stream()
				.map(MachineId::value)
				.collect(Collectors.toSet());
		List<OrderMachine> all = gateway.findAllMachinesByOrderId(orderId);
		List<OrderMachine> current = all.stream()
				.filter(machine -> !machine.deleted())
				.toList();
		Map<String,OrderMachine> removedBefore = all.stream()
				.filter(OrderMachine::deleted)
				.collect(Collectors.toMap(OrderMachine::machineId, Function.identity(), (first, second) -> first));
		Set<String> currentValues = current.stream()
				.map(OrderMachine::machineId)
				.collect(Collectors.toSet());
		List<OrderMachine> kept = current.stream()
				.filter(machine -> requestedValues.contains(machine.machineId()))
				.toList();
		List<OrderMachine> changes = new ArrayList<>(current.stream()
				.filter(machine -> !requestedValues.contains(machine.machineId()))
				.map(machine -> machine.remove(principal.userId()))
				.toList());
		Set<MachineId> added = requested.stream()
				.filter(machineId -> !currentValues.contains(machineId.value()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		resolveMachines(added, orderId, tenantId).stream()
				.map(machine -> removedBefore.containsKey(machine.id())
						? removedBefore.get(machine.id()).restore(machine, principal.userId())
						: OrderMachine.assign(order.id(), machine, principal.userId()))
				.forEach(changes::add);
		List<OrderMachine> saved = changes.isEmpty() ? List.of() : gateway.saveMachines(changes);
		List<OrderMachine> machines = new ArrayList<>(kept);
		saved.stream()
				.filter(machine -> !machine.deleted())
				.forEach(machines::add);
		presenter.present(new OrderAssignMachinesResponse(order, machines));
	}

	private Set<MachineId> machineIds(Set<String> rawIds){
		return rawIds.stream()
				.filter(id -> id != null && !id.isBlank())
				.map(id -> new MachineId(id.trim()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private List<Machine> resolveMachines(Set<MachineId> machineIds, OrderId orderId, TenantId tenantId){
		if(machineIds.isEmpty()){
			return List.of();
		}
		Map<String,Machine> found = gateway.lockMachines(machineIds, tenantId).stream()
				.collect(Collectors.toMap(Machine::id, Function.identity()));
		List<Machine> machines = new ArrayList<>();
		for(MachineId machineId : machineIds){
			Machine machine = found.get(machineId.value());
			if(machine == null || !machine.active()){
				throw new NotFoundException("Machine Not Found");
			}
			machines.add(machine);
		}
		List<MachineOccupancy> occupancies = gateway.findOccupancies(machineIds, orderId);
		if(!occupancies.isEmpty()){
			MachineOccupancy occupancy = occupancies.getFirst();
			throw new MachineBusyException("Machine " + found.get(occupancy.machineId()).name() + " is in use by "
					+ occupancy.orderNumber());
		}
		return machines;
	}

}
