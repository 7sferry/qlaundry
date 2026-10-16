package com.ferry.order.webservice.order.machines;

import com.ferry.order.core.order.machines.OrderMachinesPresenter;
import com.ferry.order.core.order.machines.OrderMachinesResponse;
import com.ferry.order.domain.order.Order;
import com.ferry.order.webservice.order.machines.OrderMachinesRestResponse.Machine;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebOrderMachinesPresenter implements OrderMachinesPresenter{
	private ResponseEntity<OrderMachinesRestResponse> responseEntity;

	@Override
	public void present(OrderMachinesResponse response){
		Order order = response.order();
		List<Machine> machines = response.machines().stream()
				.map(o -> new Machine(o.machineId(), o.machineName(), o.machineType().name()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderMachinesRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), machines));
	}

}
