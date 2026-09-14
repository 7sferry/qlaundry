package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.backfill.AnalyticsBackfillGateway;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPromotionJpaEntity;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class AnalyticsBackfillJpaGateway implements AnalyticsBackfillGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public List<OrderDomain> findOrdersAfter(String tenantId, String afterId, int limit){
		return orderJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(entity -> OrderJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<OrderItemDomain> findItemsByOrderId(OrderIdDomain orderId){
		return orderItemJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderItemJpaEntity::construct)
				.toList();
	}

	@Override
	public List<OrderPromotionDomain> findPromotionsByOrderId(OrderIdDomain orderId){
		return orderPromotionJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderPromotionJpaEntity::construct)
				.toList();
	}

	@Override
	public List<LaundryServiceDomain> findServicesAfter(String tenantId, String afterId, int limit){
		return laundryServiceJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(LaundryServiceJpaEntity::construct)
				.toList();
	}

}
