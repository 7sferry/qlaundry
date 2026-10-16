package com.ferry.order.webservice.order.process;

import com.ferry.order.core.order.process.OrderProcessPresenter;
import com.ferry.order.core.order.process.OrderProcessResponse;
import com.ferry.order.domain.order.Order;
import com.ferry.order.webservice.order.process.OrderProcessRestResponse.Machine;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderProcessPresenter implements OrderProcessPresenter{
	private ResponseEntity<OrderProcessRestResponse> responseEntity;

	@Override
	public void present(OrderProcessResponse response){
		Order order = response.order();
		List<Machine> machines = response.machines().stream()
				.map(o -> new Machine(o.machineId(), o.machineName(), o.machineType().name()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderProcessRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli(), machines));
	}

}
