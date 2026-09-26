package com.ferry.order.webservice.order.ready;

import com.ferry.order.core.order.ready.OrderReadyPresenter;
import com.ferry.order.core.order.ready.OrderReadyResponse;
import com.ferry.order.domain.order.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderReadyPresenter implements OrderReadyPresenter{
	private ResponseEntity<OrderReadyRestResponse> responseEntity;

	@Override
	public void present(OrderReadyResponse response){
		Order order = response.order();
		responseEntity = ResponseEntity.ok(new OrderReadyRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
