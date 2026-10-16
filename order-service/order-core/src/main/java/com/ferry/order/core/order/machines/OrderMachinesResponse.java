package com.ferry.order.core.order.machines;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderMachine;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderMachinesResponse(Order order, List<OrderMachine> machines){
}
