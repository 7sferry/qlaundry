package com.ferry.order.webservice.order.schedule;

import com.ferry.order.core.order.schedule.OrderSchedulePresenter;
import com.ferry.order.core.order.schedule.OrderScheduleResponse;
import com.ferry.order.webservice.order.schedule.OrderScheduleRestResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class WebOrderSchedulePresenter implements OrderSchedulePresenter{
	private ResponseEntity<OrderScheduleRestResponse> responseEntity;

	@Override
	public void present(OrderScheduleResponse response){
		List<Item> items = response.items().stream()
				.map(item -> new Item(item.orderId(), item.orderNumber(), item.customerName(), item.type(),
						item.scheduledAt().toEpochMilli(), item.status()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderScheduleRestResponse(response.date().toString(), items));
	}

}
