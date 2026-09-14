package com.ferry.order.core.order.schedule;

import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.domain.tenant.TenantIdDomain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderScheduleGateway{
	List<OrderScheduleProjection> findPickupsBetween(TenantIdDomain tenantId, Instant from, Instant to,
	                                                 Collection<OrderStatus> statuses);

	List<OrderScheduleProjection> findDeliveriesBetween(TenantIdDomain tenantId, Instant from, Instant to,
	                                                    Collection<OrderStatus> statuses);
}
