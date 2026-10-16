package com.ferry.order.core.order.process;

import com.ferry.order.core.analytics.OrderAnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;
import com.ferry.order.domain.common.Note;
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
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderProcessUseCase implements OrderProcessUseCase{
	private final OrderProcessGateway gateway;
	private final OrderAnalyticsPublisher publisher;

	@Override
	public void execute(OrderProcessRequest request, OrderAuthPrincipal principal, OrderProcessPresenter presenter){
		request.validate();
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Order order = gateway.lockById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		Note staffNotes = request.staffNotes() == null || request.staffNotes().isBlank()
				? null : new Note(request.staffNotes());
		Order changed = order.changeStatus(OrderStatus.IN_PROGRESS, staffNotes, principal.userId());
		List<Machine> machines = resolveMachines(machineIds(request.machineIds()), orderId, tenantId);
		Order saved = gateway.save(changed);
		List<OrderMachine> assigned = machines.isEmpty() ? List.of() : gateway.saveMachines(machines.stream()
				.map(machine -> OrderMachine.assign(saved.id(), machine, principal.userId()))
				.toList());
		publisher.publish(publisher.save(OrderAnalyticsEventConfig.order(OrderAnalyticsEventType.ORDER_STATUS_CHANGED,
				OrderAnalyticsMessage.from(saved, gateway.findItemsByOrderId(orderId),
						gateway.findPromotionsByOrderId(orderId)), principal.userId())));
		presenter.present(new OrderProcessResponse(saved, assigned));
	}

	private Set<MachineId> machineIds(Set<String> rawIds){
		if(rawIds == null){
			return Set.of();
		}
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
