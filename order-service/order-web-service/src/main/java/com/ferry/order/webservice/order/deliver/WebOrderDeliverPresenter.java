package com.ferry.order.webservice.order.deliver;

import com.ferry.order.core.order.deliver.OrderDeliverPresenter;
import com.ferry.order.core.order.deliver.OrderDeliverResponse;
import com.ferry.order.domain.order.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderDeliverPresenter implements OrderDeliverPresenter{
	private ResponseEntity<OrderDeliverRestResponse> responseEntity;

	@Override
	public void present(OrderDeliverResponse response){
		Order order = response.order();
		responseEntity = ResponseEntity.ok(new OrderDeliverRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
