package com.ferry.order.gateway.order;

import com.ferry.order.core.order.create.OrderCreateGateway;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.entity.ClothingTypeJpaEntity;
import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPriorityJpaEntity;
import com.ferry.order.gateway.order.entity.OrderStatusJpaEntity;
import com.ferry.order.gateway.order.entity.PaymentMethodJpaEntity;
import com.ferry.order.gateway.order.entity.PaymentStatusJpaEntity;
import com.ferry.order.gateway.order.repository.ClothingTypeJpaRepository;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPriorityJpaRepository;
import com.ferry.order.gateway.order.repository.OrderStatusJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentMethodJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentStatusJpaRepository;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceUnitJpaEntity;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class OrderCreateJpaGateway implements OrderCreateGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final OrderPriorityJpaRepository orderPriorityJpaRepository;
	private final PaymentMethodJpaRepository paymentMethodJpaRepository;
	private final PaymentStatusJpaRepository paymentStatusJpaRepository;
	private final OrderStatusJpaRepository orderStatusJpaRepository;
	private final ClothingTypeJpaRepository clothingTypeJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<LaundryServiceDomain> findServiceById(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpaEntity::construct);
	}

	@Override
	public OrderDomain save(OrderDomain order){
		String id = idGenerator.generateId();
		ServiceUnitJpaEntity unit = serviceUnitJpaRepository.getReferenceById(order.unit().getValue());
		OrderPriorityJpaEntity priority = orderPriorityJpaRepository.getReferenceById(order.priority().getValue());
		PaymentMethodJpaEntity paymentMethod = paymentMethodJpaRepository.getReferenceById(
				order.paymentMethod().getValue());
		PaymentStatusJpaEntity paymentStatus = paymentStatusJpaRepository.getReferenceById(
				order.paymentStatus().getValue());
		OrderStatusJpaEntity status = orderStatusJpaRepository.getReferenceById(order.status().getValue());
		OrderJpaEntity saved = orderJpaRepository.save(OrderJpaEntity.construct(id, order, unit, priority,
				paymentMethod, paymentStatus, status, cryptoTool));
		return OrderJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public OrderItemDomain save(OrderItemDomain item){
		String id = idGenerator.generateId();
		OrderJpaEntity order = orderJpaRepository.getReferenceById(item.orderId());
		ClothingTypeJpaEntity type = clothingTypeJpaRepository.getReferenceById(item.type().getValue());
		OrderItemJpaEntity saved = orderItemJpaRepository.save(OrderItemJpaEntity.construct(id, item, order, type));
		return OrderItemJpaEntity.construct(saved);
	}

}
