package com.ferry.order.webservice.order.complete;

import com.ferry.order.core.order.complete.OrderCompletePresenter;
import com.ferry.order.core.order.complete.OrderCompleteResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderCompleteWebPresenter implements OrderCompletePresenter{
	private ResponseEntity<OrderCompleteWebResponse> responseEntity;

	@Override
	public void present(OrderCompleteResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderCompleteWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
