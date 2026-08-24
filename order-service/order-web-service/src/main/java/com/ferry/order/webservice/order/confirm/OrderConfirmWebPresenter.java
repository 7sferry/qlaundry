package com.ferry.order.webservice.order.confirm;

import com.ferry.order.core.order.confirm.OrderConfirmPresenter;
import com.ferry.order.core.order.confirm.OrderConfirmResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderConfirmWebPresenter implements OrderConfirmPresenter{
	private ResponseEntity<OrderConfirmWebResponse> responseEntity;

	@Override
	public void present(OrderConfirmResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderConfirmWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
