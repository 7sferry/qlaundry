package com.ferry.order.core.order.cancel;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCancelUseCase{
	void execute(OrderCancelRequest request, OrderAuthPrincipal principal, OrderCancelPresenter presenter);
}
