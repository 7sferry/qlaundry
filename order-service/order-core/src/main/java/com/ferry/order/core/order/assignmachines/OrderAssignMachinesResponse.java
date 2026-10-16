package com.ferry.order.core.order.assignmachines;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderMachine;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderAssignMachinesResponse(Order order, List<OrderMachine> machines){
}
