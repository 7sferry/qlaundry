package com.ferry.order.core.order.detail;

import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderDetailUseCase implements OrderDetailUseCase{
	private final OrderDetailGateway gateway;

	@Override
	public void execute(OrderDetailRequest request, OrderAuthPrincipal principal, OrderDetailPresenter presenter){
		request.validate();
		OrderIdDomain orderId = new OrderIdDomain(request.orderId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		OrderDomain order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		List<OrderItemDomain> items = gateway.findItemsByOrderId(orderId);
		presenter.present(new OrderDetailResponse(order, items));
	}

}
