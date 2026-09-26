package com.ferry.order.gateway.order;

import com.ferry.order.core.order.confirm.OrderConfirmRequest;
import com.ferry.order.core.order.confirm.OrderConfirmUseCase;
import com.ferry.order.core.order.create.OrderCreateGateway;
import com.ferry.order.core.order.pickup.OrderPickupRequest;
import com.ferry.order.core.order.pickup.OrderPickupUseCase;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.order.gateway.order.entity.OrderItemJpa;
import com.ferry.order.gateway.order.entity.OrderJpa;
import com.ferry.order.gateway.order.entity.OrderPromotionJpa;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaOrderCreateGateway implements OrderCreateGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;
	private final OrderConfirmUseCase orderConfirmUseCase;
	private final OrderPickupUseCase orderPickupUseCase;

	@Override
	public Optional<LaundryService> findServiceById(LaundryServiceId serviceId, TenantId tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpa::construct);
	}

	@Override
	public Order save(Order order){
		String id = idGenerator.generateId();
		OrderJpa saved = orderJpaRepository.save(OrderJpa.construct(id, order, cryptoTool));
		return OrderJpa.construct(saved, cryptoTool);
	}

	@Override
	public OrderItem save(OrderItem item){
		String id = idGenerator.generateId();
		OrderJpa order = orderJpaRepository.getReferenceById(item.orderId());
		OrderItemJpa saved = orderItemJpaRepository.save(OrderItemJpa.construct(id, item, order));
		return OrderItemJpa.construct(saved);
	}

	@Override
	public OrderPromotion save(OrderPromotion promotion){
		String id = idGenerator.generateId();
		OrderJpa order = orderJpaRepository.getReferenceById(promotion.orderId());
		OrderPromotionJpa saved = orderPromotionJpaRepository.save(
				OrderPromotionJpa.construct(id, promotion, order));
		return OrderPromotionJpa.construct(saved);
	}

	@Override
	public Order markPickedUp(Order order, OrderAuthPrincipal principal){
		Order[] confirmed = new Order[1];
		orderConfirmUseCase.execute(new OrderConfirmRequest(order.id(), null), principal,
				response -> confirmed[0] = response.order());
		Order[] pickedUp = new Order[1];
		orderPickupUseCase.execute(new OrderPickupRequest(confirmed[0].id(), null), principal,
				response -> pickedUp[0] = response.order());
		return pickedUp[0];
	}

}
