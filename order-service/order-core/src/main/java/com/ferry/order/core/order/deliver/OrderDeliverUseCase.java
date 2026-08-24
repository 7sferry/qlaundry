package com.ferry.order.core.order.deliver;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderDeliverUseCase{
	void execute(OrderDeliverRequest request, OrderAuthPrincipal principal, OrderDeliverPresenter presenter);
}
