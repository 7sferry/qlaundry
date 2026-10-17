package com.ferry.order.core.order.detail;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.order.OrderStaff;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderDetailResponse(
	Order order,
	List<OrderItem> items,
	List<OrderPromotion> promotions,
	List<OrderMachine> machines,
	List<OrderStaff> staffs){
}
