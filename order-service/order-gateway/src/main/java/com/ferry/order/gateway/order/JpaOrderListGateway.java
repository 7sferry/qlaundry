package com.ferry.order.gateway.order;

import com.ferry.order.core.order.list.OrderListGateway;
import com.ferry.order.domain.order.*;
import com.ferry.order.gateway.order.entity.OrderItemJpa;
import com.ferry.order.gateway.order.entity.OrderJpa;
import com.ferry.order.gateway.order.entity.OrderPromotionJpa;
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
public class JpaOrderListGateway implements OrderListGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public CursorFetch<Order> findByFilter(OrderFilter filter){
		List<OrderJpa> raw = fetchByFilter(filter);
		List<Order> rows = raw.stream().map(entity -> OrderJpa.construct(entity, cryptoTool)).toList();
		return CursorFetch.of(rows, filter.pageSize(), filter.pageDirection());
	}

	private List<OrderJpa> fetchByFilter(OrderFilter filter){
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
	public List<OrderItem> findItemsByOrderIds(Set<String> orderIds){
		return orderItemJpaRepository.findByOrderIdInAndDeletedIsFalseOrderById(orderIds).stream()
				.map(OrderItemJpa::construct)
				.toList();
	}

	@Override
	public List<OrderPromotion> findPromotionsByOrderIds(Set<String> orderIds){
		return orderPromotionJpaRepository.findByOrderIdInAndDeletedIsFalseOrderById(orderIds).stream()
				.map(OrderPromotionJpa::construct)
				.toList();
	}

}
