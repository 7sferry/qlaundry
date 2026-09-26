package com.ferry.order.core.order.detail;

import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.tenant.TenantId;
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
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Order order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		List<OrderItem> items = gateway.findItemsByOrderId(orderId);
		List<OrderPromotion> promotions = gateway.findPromotionsByOrderId(orderId);
		presenter.present(new OrderDetailResponse(order, items, promotions));
	}

}
