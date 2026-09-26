package com.ferry.order.webservice.order.schedule;

import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleType;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderScheduleRestResponse(
	String date,
	List<Item> items){
	public record Item(
		String orderId,
		String orderNumber,
		String customerName,
		OrderScheduleType type,
		long scheduledAt,
		OrderStatus status){
	}
}
