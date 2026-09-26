package com.ferry.order.gateway.order;

import com.ferry.order.core.order.deliver.OrderDeliverGateway;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.order.entity.OrderItemJpa;
import com.ferry.order.gateway.order.entity.OrderJpa;
import com.ferry.order.gateway.order.entity.OrderPromotionJpa;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaOrderDeliverGateway implements OrderDeliverGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<Order> findById(OrderId orderId, TenantId tenantId){
		return orderJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(orderId.value(), tenantId.value())
				.map(entity -> OrderJpa.construct(entity, cryptoTool));
	}

	@Override
	public Order save(Order order){
		OrderJpa saved = orderJpaRepository.saveAndFlush(OrderJpa.construct(order.id(), order, cryptoTool));
		return OrderJpa.construct(saved, cryptoTool);
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

}
