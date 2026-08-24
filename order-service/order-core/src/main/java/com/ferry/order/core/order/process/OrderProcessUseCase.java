package com.ferry.order.core.order.process;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderProcessUseCase{
	void execute(OrderProcessRequest request, OrderAuthPrincipal principal, OrderProcessPresenter presenter);
}
