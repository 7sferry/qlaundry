package com.ferry.order.gateway.order;

import com.ferry.order.core.order.list.OrderListGateway;
import com.ferry.order.domain.order.*;
import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPromotionJpaEntity;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public CursorFetch<OrderDomain> findByFilter(OrderFilter filter){
		List<OrderJpaEntity> raw = fetchByFilter(filter);
		List<OrderDomain> rows = raw.stream().map(entity -> OrderJpaEntity.construct(entity, cryptoTool)).toList();
		return CursorFetch.of(rows, filter.pageSize(), filter.pageDirection());
	}

	private List<OrderJpaEntity> fetchByFilter(OrderFilter filter){
		Pageable pageable = PageRequest.ofSize(filter.pageSize() + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == OrderListSortBy.CUSTOMER_NAME){
			return useAfterQuery
					? orderJpaRepository.findAfterByCustomerName(filter, pageable)
					: orderJpaRepository.findBeforeByCustomerName(filter, pageable);
		}
		return useAfterQuery
				? orderJpaRepository.findAfterById(filter, pageable)
				: orderJpaRepository.findBeforeById(filter, pageable);
	}

	@Override
	public List<OrderItemDomain> findItemsByOrderIds(Set<String> orderIds){
		return orderItemJpaRepository.findByOrderIdInAndDeletedIsFalseOrderById(orderIds).stream()
				.map(OrderItemJpaEntity::construct)
				.toList();
	}

	@Override
	public List<OrderPromotionDomain> findPromotionsByOrderIds(Set<String> orderIds){
		return orderPromotionJpaRepository.findByOrderIdInAndDeletedIsFalseOrderById(orderIds).stream()
				.map(OrderPromotionJpaEntity::construct)
				.toList();
	}

}
