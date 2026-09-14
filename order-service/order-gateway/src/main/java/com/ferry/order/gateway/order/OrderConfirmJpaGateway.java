package com.ferry.order.gateway.order;

import com.ferry.order.core.order.confirm.OrderConfirmGateway;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPromotionJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPriorityJpaEntity;
import com.ferry.order.gateway.order.entity.OrderStatusJpaEntity;
import com.ferry.order.gateway.order.entity.PaymentMethodJpaEntity;
import com.ferry.order.gateway.order.entity.PaymentStatusJpaEntity;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPriorityJpaRepository;
import com.ferry.order.gateway.order.repository.OrderStatusJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentMethodJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentStatusJpaRepository;
import com.ferry.order.gateway.service.entity.ServiceUnitJpaEntity;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class OrderConfirmJpaGateway implements OrderConfirmGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final OrderPriorityJpaRepository orderPriorityJpaRepository;
	private final PaymentMethodJpaRepository paymentMethodJpaRepository;
	private final PaymentStatusJpaRepository paymentStatusJpaRepository;
	private final OrderStatusJpaRepository orderStatusJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<OrderDomain> findById(OrderIdDomain orderId, TenantIdDomain tenantId){
		return orderJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(orderId.value(), tenantId.value())
				.map(entity -> OrderJpaEntity.construct(entity, cryptoTool));
	}

	@Override
	public OrderDomain save(OrderDomain order){
		ServiceUnitJpaEntity unit = serviceUnitJpaRepository.getReferenceById(order.unit().getValue());
		OrderPriorityJpaEntity priority = orderPriorityJpaRepository.getReferenceById(order.priority().getValue());
		PaymentMethodJpaEntity paymentMethod = paymentMethodJpaRepository.getReferenceById(
				order.paymentMethod().getValue());
		PaymentStatusJpaEntity paymentStatus = paymentStatusJpaRepository.getReferenceById(
				order.paymentStatus().getValue());
		OrderStatusJpaEntity status = orderStatusJpaRepository.getReferenceById(order.status().getValue());
		OrderJpaEntity saved = orderJpaRepository.saveAndFlush(OrderJpaEntity.construct(order.id(), order, unit,
				priority, paymentMethod, paymentStatus, status, cryptoTool));
		return OrderJpaEntity.construct(saved, cryptoTool);
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

}
