package com.ferry.order.core.order.create;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCreateUseCase{
	void execute(OrderCreateRequest request, OrderAuthPrincipal principal, OrderCreatePresenter presenter);
}
