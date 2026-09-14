package com.ferry.order.gateway.order;

import com.ferry.order.core.order.schedule.OrderScheduleGateway;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.domain.tenant.TenantIdDomain;
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
public class OrderScheduleJpaGateway implements OrderScheduleGateway{
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public List<OrderScheduleProjection> findPickupsBetween(TenantIdDomain tenantId, Instant from, Instant to,
	                                                        Collection<OrderStatus> statuses){
		return orderJpaRepository.findPickupSchedule(tenantId.value(), from, to,
				statuses.stream().map(OrderStatus::getValue).toList());
	}

	@Override
	public List<OrderScheduleProjection> findDeliveriesBetween(TenantIdDomain tenantId, Instant from, Instant to,
	                                                           Collection<OrderStatus> statuses){
		return orderJpaRepository.findDeliverySchedule(tenantId.value(), from, to,
				statuses.stream().map(OrderStatus::getValue).toList());
	}

}
