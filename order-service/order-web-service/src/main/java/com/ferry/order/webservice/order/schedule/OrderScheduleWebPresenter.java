package com.ferry.order.webservice.order.schedule;

import com.ferry.order.core.order.schedule.OrderSchedulePresenter;
import com.ferry.order.core.order.schedule.OrderScheduleResponse;
import com.ferry.order.webservice.order.schedule.OrderScheduleWebResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class OrderScheduleWebPresenter implements OrderSchedulePresenter{
	private ResponseEntity<OrderScheduleWebResponse> responseEntity;

	@Override
	public void present(OrderScheduleResponse response){
		List<Item> items = response.items().stream()
				.map(item -> new Item(item.orderId(), item.orderNumber(), item.customerName(), item.type(),
						item.scheduledAt().toEpochMilli(), item.status()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderScheduleWebResponse(response.date().toString(), items));
	}

}
