package com.ferry.order.webservice.order.pickup;

import com.ferry.order.core.order.pickup.OrderPickupPresenter;
import com.ferry.order.core.order.pickup.OrderPickupResponse;
import com.ferry.order.domain.order.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderPickupPresenter implements OrderPickupPresenter{
	private ResponseEntity<OrderPickupRestResponse> responseEntity;

	@Override
	public void present(OrderPickupResponse response){
		Order order = response.order();
		responseEntity = ResponseEntity.ok(new OrderPickupRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
