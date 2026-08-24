package com.ferry.order.core.order.list;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderListUseCase{
	void execute(OrderListRequest request, OrderAuthPrincipal principal, OrderListPresenter presenter);
}
