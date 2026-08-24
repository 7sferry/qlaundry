package com.ferry.order.gateway.order;

import com.ferry.order.core.order.ready.OrderReadyGateway;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPriorityJpaEntity;
import com.ferry.order.gateway.order.entity.OrderStatusJpaEntity;
import com.ferry.order.gateway.order.entity.PaymentMethodJpaEntity;
import com.ferry.order.gateway.order.entity.PaymentStatusJpaEntity;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPriorityJpaRepository;
import com.ferry.order.gateway.order.repository.OrderStatusJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentMethodJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentStatusJpaRepository;
import com.ferry.order.gateway.service.entity.ServiceUnitJpaEntity;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class OrderReadyJpaGateway implements OrderReadyGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final OrderPriorityJpaRepository orderPriorityJpaRepository;
	private final PaymentMethodJpaRepository paymentMethodJpaRepository;
	private final PaymentStatusJpaRepository paymentStatusJpaRepository;
	private final OrderStatusJpaRepository orderStatusJpaRepository;
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
		OrderJpaEntity saved = orderJpaRepository.save(OrderJpaEntity.construct(order.id(), order, unit, priority,
				paymentMethod, paymentStatus, status, cryptoTool));
		return OrderJpaEntity.construct(saved, cryptoTool);
	}

}
