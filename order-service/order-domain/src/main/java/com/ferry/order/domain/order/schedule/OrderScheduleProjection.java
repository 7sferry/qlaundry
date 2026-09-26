package com.ferry.order.domain.order.schedule;

import com.ferry.order.domain.order.OrderStatus;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderScheduleProjection(
	String orderId,
	String orderNumber,
	String customerName,
	Instant scheduledAt,
	short statusId){

	public OrderStatus status(){
		return OrderStatus.fromValue(statusId).orElseThrow();
	}

}
