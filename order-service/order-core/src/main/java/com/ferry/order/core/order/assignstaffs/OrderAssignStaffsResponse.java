package com.ferry.order.core.order.assignstaffs;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderStaff;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderAssignStaffsResponse(Order order, List<OrderStaff> staffs){
}
