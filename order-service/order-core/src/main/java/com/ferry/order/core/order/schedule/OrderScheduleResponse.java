package com.ferry.order.core.order.schedule;

import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderScheduleResponse(
	LocalDate date,
	List<Item> items){

	public record Item(
		String orderId,
		String orderNumber,
		String customerName,
		OrderScheduleType type,
		Instant scheduledAt,
		OrderStatus status){
	}

}
