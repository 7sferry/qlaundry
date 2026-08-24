package com.ferry.order.webservice.order.ready;

import com.ferry.order.core.order.ready.OrderReadyPresenter;
import com.ferry.order.core.order.ready.OrderReadyResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderReadyWebPresenter implements OrderReadyPresenter{
	private ResponseEntity<OrderReadyWebResponse> responseEntity;

	@Override
	public void present(OrderReadyResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderReadyWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
