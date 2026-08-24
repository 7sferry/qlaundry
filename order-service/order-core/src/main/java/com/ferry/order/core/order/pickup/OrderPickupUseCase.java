package com.ferry.order.core.order.pickup;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPickupUseCase{
	void execute(OrderPickupRequest request, OrderAuthPrincipal principal, OrderPickupPresenter presenter);
}
