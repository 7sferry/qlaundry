package com.ferry.order.core.order.list;

import com.ferry.utils.pagination.CursorCodec;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.CursorPage;
import com.ferry.utils.pagination.CursorPaginator;
import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
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
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		SortBy sortBy = request.sortBy() == null ? SortBy.ID : request.sortBy();
		SortDirection sortDir = request.sortDir() == null ? SortDirection.DESC : request.sortDir();
		PageDirection direction = request.direction() == null ? PageDirection.NEXT : request.direction();
		PageCursor cursor = request.cursor() == null ? null : CursorCodec.decode(request.cursor());
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
				.build();
		CursorFetch<OrderDomain> fetch = gateway.findByFilter(filter);
		CursorPage<OrderDomain> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> List.of(sortBy == SortBy.NAME ? row.customerNameValue() : row.id(), row.id()));
		List<OrderDomain> orders = page.items();
		Set<String> orderIds = orders.stream().map(OrderDomain::id).collect(Collectors.toSet());
		Map<String, List<OrderItemDomain>> itemsByOrderId = getItemsByOrderId(orderIds);
		presenter.present(new OrderListResponse(orders, itemsByOrderId, page.nextCursor(), page.prevCursor()));
	}

	private Map<String, List<OrderItemDomain>> getItemsByOrderId(Set<String> orderIds){
		if(orderIds.isEmpty()){
			return Map.of();
		}
		return gateway.findItemsByOrderIds(orderIds).stream()
				.collect(Collectors.groupingBy(OrderItemDomain::orderId));
	}

}
