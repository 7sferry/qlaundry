package com.ferry.order.webservice.order.cancel;

import com.ferry.order.core.order.cancel.OrderCancelPresenter;
import com.ferry.order.core.order.cancel.OrderCancelResponse;
import com.ferry.order.domain.order.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderCancelPresenter implements OrderCancelPresenter{
	private ResponseEntity<OrderCancelRestResponse> responseEntity;

	@Override
	public void present(OrderCancelResponse response){
		Order order = response.order();
		responseEntity = ResponseEntity.ok(new OrderCancelRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
