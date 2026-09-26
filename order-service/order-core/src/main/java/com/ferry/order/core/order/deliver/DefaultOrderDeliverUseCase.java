package com.ferry.order.core.order.deliver;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderDeliverUseCase implements OrderDeliverUseCase{
	private final OrderDeliverGateway gateway;
	private final OrderAnalyticsPublisher publisher;

	@Override
	public void execute(OrderDeliverRequest request, OrderAuthPrincipal principal, OrderDeliverPresenter presenter){
		request.validate();
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Order order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		Note staffNotes = request.staffNotes() == null || request.staffNotes().isBlank()
				? null : new Note(request.staffNotes());
		Order saved = gateway.save(order.changeStatus(OrderStatus.OUT_FOR_DELIVERY, staffNotes,
				principal.userId()));
		publisher.publish(publisher.save(AnalyticsEventConfig.order(AnalyticsEventType.ORDER_STATUS_CHANGED,
				OrderAnalyticsMessage.from(saved, gateway.findItemsByOrderId(orderId),
						gateway.findPromotionsByOrderId(orderId)), principal.userId())));
		presenter.present(new OrderDeliverResponse(saved));
	}

}
