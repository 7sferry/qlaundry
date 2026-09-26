package com.ferry.order.core.order.list;

import com.ferry.order.domain.order.OrderListSortBy;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.CursorPage;
import com.ferry.utils.pagination.CursorPaginator;
import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.PaginationConstant;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderListUseCase implements OrderListUseCase{
	private final OrderListGateway gateway;

	@Override
	public void execute(OrderListRequest request, OrderAuthPrincipal principal, OrderListPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		SortDirection sortDir = request.sortDir() == null ? SortDirection.DESC : request.sortDir();
		PageDirection direction = PageDirection.direction(request.before());
		PageCursor cursor = PageCursor.cursor(request.after(), request.before());
		OrderListSortBy sortBy = request.sortBy() == null ? OrderListSortBy.ID : request.sortBy();
		int pageSize = PaginationConstant.resolvePageSize(request.pageSize());
		OrderFilter filter = OrderFilter.builder()
				.tenantId(tenantId.value())
				.status(request.status())
				.priority(request.priority())
				.customerId(request.customerId())
				.orderNumber(request.orderNumber())
				.from(request.from() == null ? null : Instant.ofEpochMilli(request.from()))
				.to(request.to() == null ? null : Instant.ofEpochMilli(request.to()))
				.sortBy(sortBy)
				.sortDir(sortDir)
				.pageDirection(direction)
				.cursor(cursor)
				.pageSize(pageSize)
				.build();
		CursorFetch<Order> fetch = gateway.findByFilter(filter);
		CursorPage<Order> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> List.of(sortBy == OrderListSortBy.CUSTOMER_NAME ? row.customerNameValue() : row.id(), row.id()));
		List<Order> orders = page.items();
		Set<String> orderIds = orders.stream().map(Order::id).collect(Collectors.toSet());
		Map<String, List<OrderItem>> itemsByOrderId = getItemsByOrderId(orderIds);
		Map<String, List<OrderPromotion>> promotionsByOrderId = getPromotionsByOrderId(orderIds);
		presenter.present(new OrderListResponse(orders, itemsByOrderId, promotionsByOrderId, page.nextCursor(),
				page.prevCursor()));
	}

	private Map<String, List<OrderItem>> getItemsByOrderId(Set<String> orderIds){
		if(orderIds.isEmpty()){
			return Map.of();
		}
		return gateway.findItemsByOrderIds(orderIds).stream()
				.collect(Collectors.groupingBy(OrderItem::orderId));
	}

	private Map<String, List<OrderPromotion>> getPromotionsByOrderId(Set<String> orderIds){
		if(orderIds.isEmpty()){
			return Map.of();
		}
		return gateway.findPromotionsByOrderIds(orderIds).stream()
				.collect(Collectors.groupingBy(OrderPromotion::orderId));
	}

}
