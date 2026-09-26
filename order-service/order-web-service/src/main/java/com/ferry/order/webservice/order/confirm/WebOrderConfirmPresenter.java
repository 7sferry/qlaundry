package com.ferry.order.webservice.order.confirm;

import com.ferry.order.core.order.confirm.OrderConfirmPresenter;
import com.ferry.order.core.order.confirm.OrderConfirmResponse;
import com.ferry.order.domain.order.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderConfirmPresenter implements OrderConfirmPresenter{
	private ResponseEntity<OrderConfirmRestResponse> responseEntity;

	@Override
	public void present(OrderConfirmResponse response){
		Order order = response.order();
		responseEntity = ResponseEntity.ok(new OrderConfirmRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
