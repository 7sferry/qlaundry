package com.ferry.analytics.webservice.event.order;

import com.ferry.analytics.core.event.order.OrderEventPresenter;
import com.ferry.analytics.core.event.order.OrderEventResponse;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class OrderEventStreamPresenter implements OrderEventPresenter{
	private OrderEventResponse response;

	@Override
	public void present(OrderEventResponse response){
		this.response = response;
	}
}
