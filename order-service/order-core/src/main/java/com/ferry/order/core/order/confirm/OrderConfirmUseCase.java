package com.ferry.order.core.order.confirm;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderConfirmUseCase{
	void execute(OrderConfirmRequest request, OrderAuthPrincipal principal, OrderConfirmPresenter presenter);
}
