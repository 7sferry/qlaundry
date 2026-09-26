package com.ferry.order.core.order.payment;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.UnsupportedPaymentMethodException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderPaymentUseCase implements OrderPaymentUseCase{
	private final OrderPaymentGateway gateway;
	private final OrderAnalyticsPublisher publisher;

	@Override
	public void execute(OrderPaymentRequest request, OrderAuthPrincipal principal, OrderPaymentPresenter presenter){
		request.validate();
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		validatePaymentMethod(request.paymentMethod());
		Order order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		Order saved = gateway.save(order.markPaid(principal.userId()));
		publisher.publish(publisher.save(AnalyticsEventConfig.order(AnalyticsEventType.ORDER_PAID,
				OrderAnalyticsMessage.from(saved, gateway.findItemsByOrderId(orderId),
						gateway.findPromotionsByOrderId(orderId)), principal.userId())));
		presenter.present(new OrderPaymentResponse(saved));
	}

	private void validatePaymentMethod(PaymentMethod paymentMethod){
		if(paymentMethod != null && paymentMethod != PaymentMethod.CASH){
			throw new UnsupportedPaymentMethodException("Only cash payment is supported for now");
		}
	}

}
