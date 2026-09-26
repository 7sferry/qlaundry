package com.ferry.order.gateway.order;

import com.ferry.order.core.order.schedule.OrderScheduleGateway;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class JpaOrderScheduleGateway implements OrderScheduleGateway{
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public List<OrderScheduleProjection> findPickupsBetween(TenantId tenantId, Instant from, Instant to,
	                                                        Collection<OrderStatus> statuses){
		return orderJpaRepository.findPickupSchedule(tenantId.value(), from, to,
				statuses.stream().map(OrderStatus::getValue).toList());
	}

	@Override
	public List<OrderScheduleProjection> findDeliveriesBetween(TenantId tenantId, Instant from, Instant to,
	                                                           Collection<OrderStatus> statuses){
		return orderJpaRepository.findDeliverySchedule(tenantId.value(), from, to,
				statuses.stream().map(OrderStatus::getValue).toList());
	}

}
