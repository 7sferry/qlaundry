package com.ferry.order.core.order.schedule;

import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.domain.tenant.TenantId;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderScheduleGateway{
	List<OrderScheduleProjection> findPickupsBetween(TenantId tenantId, Instant from, Instant to,
	                                                 Collection<OrderStatus> statuses);

	List<OrderScheduleProjection> findDeliveriesBetween(TenantId tenantId, Instant from, Instant to,
	                                                    Collection<OrderStatus> statuses);
}
