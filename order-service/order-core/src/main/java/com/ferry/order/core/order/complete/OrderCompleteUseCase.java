package com.ferry.order.core.order.complete;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCompleteUseCase{
	void execute(OrderCompleteRequest request, OrderAuthPrincipal principal, OrderCompletePresenter presenter);
}
