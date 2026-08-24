package com.ferry.order.webservice.order.deliver;

import com.ferry.order.core.order.deliver.OrderDeliverPresenter;
import com.ferry.order.core.order.deliver.OrderDeliverResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderDeliverWebPresenter implements OrderDeliverPresenter{
	private ResponseEntity<OrderDeliverWebResponse> responseEntity;

	@Override
	public void present(OrderDeliverResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderDeliverWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
