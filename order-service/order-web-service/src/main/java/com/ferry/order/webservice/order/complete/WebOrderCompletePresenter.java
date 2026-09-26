package com.ferry.order.webservice.order.complete;

import com.ferry.order.core.order.complete.OrderCompletePresenter;
import com.ferry.order.core.order.complete.OrderCompleteResponse;
import com.ferry.order.domain.order.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderCompletePresenter implements OrderCompletePresenter{
	private ResponseEntity<OrderCompleteRestResponse> responseEntity;

	@Override
	public void present(OrderCompleteResponse response){
		Order order = response.order();
		responseEntity = ResponseEntity.ok(new OrderCompleteRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
