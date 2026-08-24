package com.ferry.order.core.order.payment;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPaymentUseCase{
	void execute(OrderPaymentRequest request, OrderAuthPrincipal principal, OrderPaymentPresenter presenter);
}
