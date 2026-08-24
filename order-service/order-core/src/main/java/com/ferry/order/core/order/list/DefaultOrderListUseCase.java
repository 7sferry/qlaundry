package com.ferry.order.core.order.list;

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
		OrderFilter filter = OrderFilter.builder()
				.tenantId(tenantId.value())
				.status(request.status())
				.priority(request.priority())
				.customerId(request.customerId())
				.orderNumber(request.orderNumber())
				.from(request.from() == null ? null : Instant.ofEpochMilli(request.from()))
				.to(request.to() == null ? null : Instant.ofEpochMilli(request.to()))
				.build();
		List<OrderDomain> orders = gateway.findByFilter(filter);
		Set<String> orderIds = orders.stream().map(OrderDomain::id).collect(Collectors.toSet());
		Map<String, List<OrderItemDomain>> itemsByOrderId = getItemsByOrderId(orderIds);
		presenter.present(new OrderListResponse(orders, itemsByOrderId));
	}

	private Map<String, List<OrderItemDomain>> getItemsByOrderId(Set<String> orderIds){
		if(orderIds.isEmpty()){
			return Map.of();
		}
		return gateway.findItemsByOrderIds(orderIds).stream()
				.collect(Collectors.groupingBy(OrderItemDomain::orderId));
	}

}
