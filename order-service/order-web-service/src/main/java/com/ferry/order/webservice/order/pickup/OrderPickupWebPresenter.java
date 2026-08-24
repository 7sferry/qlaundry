package com.ferry.order.webservice.order.pickup;

import com.ferry.order.core.order.pickup.OrderPickupPresenter;
import com.ferry.order.core.order.pickup.OrderPickupResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderPickupWebPresenter implements OrderPickupPresenter{
	private ResponseEntity<OrderPickupWebResponse> responseEntity;

	@Override
	public void present(OrderPickupResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderPickupWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
