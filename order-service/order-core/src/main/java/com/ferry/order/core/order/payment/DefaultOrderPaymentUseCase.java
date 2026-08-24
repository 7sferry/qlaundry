package com.ferry.order.core.order.payment;

import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.UnsupportedPaymentMethodException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderPaymentUseCase implements OrderPaymentUseCase{
	private final OrderPaymentGateway gateway;

	@Override
	public void execute(OrderPaymentRequest request, OrderAuthPrincipal principal, OrderPaymentPresenter presenter){
		request.validate();
		OrderIdDomain orderId = new OrderIdDomain(request.orderId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		validatePaymentMethod(request.paymentMethod());
		OrderDomain order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		OrderDomain saved = gateway.save(order.markPaid(principal.userId()));
		presenter.present(new OrderPaymentResponse(saved));
	}

	private void validatePaymentMethod(PaymentMethod paymentMethod){
		if(paymentMethod != null && paymentMethod != PaymentMethod.CASH){
			throw new UnsupportedPaymentMethodException("Only cash payment is supported for now");
		}
	}

}
