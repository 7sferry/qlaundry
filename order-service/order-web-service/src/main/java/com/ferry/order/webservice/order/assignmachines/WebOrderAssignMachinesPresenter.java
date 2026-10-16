package com.ferry.order.webservice.order.assignmachines;

import com.ferry.order.core.order.assignmachines.OrderAssignMachinesPresenter;
import com.ferry.order.core.order.assignmachines.OrderAssignMachinesResponse;
import com.ferry.order.domain.order.Order;
import com.ferry.order.webservice.order.assignmachines.OrderAssignMachinesRestResponse.Machine;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebOrderAssignMachinesPresenter implements OrderAssignMachinesPresenter{
	private ResponseEntity<OrderAssignMachinesRestResponse> responseEntity;

	@Override
	public void present(OrderAssignMachinesResponse response){
		Order order = response.order();
		List<Machine> machines = response.machines().stream()
				.map(o -> new Machine(o.machineId(), o.machineName(), o.machineType().name()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderAssignMachinesRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), machines));
	}

}
