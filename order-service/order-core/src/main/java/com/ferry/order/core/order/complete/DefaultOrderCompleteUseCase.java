package com.ferry.order.core.order.complete;

import com.ferry.order.core.analytics.OrderAnalyticsEvent;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderCompleteUseCase implements OrderCompleteUseCase{
	private final OrderCompleteGateway gateway;
	private final OrderAnalyticsPublisher publisher;

	@Override
	public void execute(OrderCompleteRequest request, OrderAuthPrincipal principal, OrderCompletePresenter presenter){
		request.validate();
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Order order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		Note staffNotes = request.staffNotes() == null || request.staffNotes().isBlank()
				? null : new Note(request.staffNotes());
		Order completed = order.changeStatus(OrderStatus.COMPLETED, staffNotes, principal.userId());
		if(completed.paymentMethod() == PaymentMethod.CASH && completed.paymentStatus() == PaymentStatus.UNPAID){
			completed = completed.markPaid(principal.userId());
		}
		Order saved = gateway.save(completed);
		publisher.publish(publisher.save(OrderAnalyticsEvent.order(OrderAnalyticsEventType.ORDER_STATUS_CHANGED,
				OrderAnalyticsMessage.from(saved, gateway.findItemsByOrderId(orderId),
						gateway.findPromotionsByOrderId(orderId)), principal.userId())));
		presenter.present(new OrderCompleteResponse(saved));
	}

}
