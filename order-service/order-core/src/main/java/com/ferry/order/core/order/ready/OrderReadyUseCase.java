package com.ferry.order.core.order.ready;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderReadyUseCase{
	void execute(OrderReadyRequest request, OrderAuthPrincipal principal, OrderReadyPresenter presenter);
}
