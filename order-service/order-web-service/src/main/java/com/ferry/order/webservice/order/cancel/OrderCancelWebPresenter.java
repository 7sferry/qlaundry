package com.ferry.order.webservice.order.cancel;

import com.ferry.order.core.order.cancel.OrderCancelPresenter;
import com.ferry.order.core.order.cancel.OrderCancelResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderCancelWebPresenter implements OrderCancelPresenter{
	private ResponseEntity<OrderCancelWebResponse> responseEntity;

	@Override
	public void present(OrderCancelResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderCancelWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
