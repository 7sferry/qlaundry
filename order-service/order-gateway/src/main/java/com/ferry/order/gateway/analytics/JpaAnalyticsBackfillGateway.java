package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.backfill.AnalyticsBackfillGateway;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.gateway.order.entity.OrderItemJpa;
import com.ferry.order.gateway.order.entity.OrderJpa;
import com.ferry.order.gateway.order.entity.OrderPromotionJpa;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
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
public class JpaAnalyticsBackfillGateway implements AnalyticsBackfillGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public List<Order> findOrdersAfter(String tenantId, String afterId, int limit){
		return orderJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(entity -> OrderJpa.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<OrderItem> findItemsByOrderId(OrderId orderId){
		return orderItemJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderItemJpa::construct)
				.toList();
	}

	@Override
	public List<OrderPromotion> findPromotionsByOrderId(OrderId orderId){
		return orderPromotionJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderPromotionJpa::construct)
				.toList();
	}

	@Override
	public List<LaundryService> findServicesAfter(String tenantId, String afterId, int limit){
		return laundryServiceJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(LaundryServiceJpa::construct)
				.toList();
	}

}
