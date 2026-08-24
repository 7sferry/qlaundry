package com.ferry.order.gateway.order;

import com.ferry.order.core.order.list.OrderListGateway;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class OrderListJpaGateway implements OrderListGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public List<OrderDomain> findByFilter(OrderFilter filter){
		return orderJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> OrderJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<OrderItemDomain> findItemsByOrderIds(Set<String> orderIds){
		return orderItemJpaRepository.findByOrderIdInAndDeletedIsFalseOrderById(orderIds).stream()
				.map(OrderItemJpaEntity::construct)
				.toList();
	}

}
