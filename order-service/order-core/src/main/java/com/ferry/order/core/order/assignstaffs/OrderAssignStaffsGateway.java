package com.ferry.order.core.order.assignstaffs;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderStaff;
import com.ferry.order.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderAssignStaffsGateway{
	Optional<Order> lockById(OrderId orderId, TenantId tenantId);

	List<OrderStaff> findAllStaffsByOrderId(OrderId orderId);

	List<OrderStaff> saveStaffs(List<OrderStaff> staffs);
}
