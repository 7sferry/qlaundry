package com.ferry.order.webservice.order.process;

import com.ferry.order.core.order.process.OrderProcessPresenter;
import com.ferry.order.core.order.process.OrderProcessResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderProcessWebPresenter implements OrderProcessPresenter{
	private ResponseEntity<OrderProcessWebResponse> responseEntity;

	@Override
	public void present(OrderProcessResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderProcessWebResponse(order.id(), order.orderNumberValue(),
				order.status().name(), order.staffNotesValue(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.updatedAt().toEpochMilli()));
	}

}
