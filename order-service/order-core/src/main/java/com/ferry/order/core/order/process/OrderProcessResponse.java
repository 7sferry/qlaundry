package com.ferry.order.core.order.process;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderMachine;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderProcessResponse(Order order, List<OrderMachine> machines){
}
