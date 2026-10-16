package com.ferry.order.core.order.machines;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderMachinesUseCase{
	void execute(OrderMachinesRequest request, OrderAuthPrincipal principal, OrderMachinesPresenter presenter);
}
