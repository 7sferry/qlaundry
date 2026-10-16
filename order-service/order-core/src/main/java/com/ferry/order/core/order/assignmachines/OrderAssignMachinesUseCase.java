package com.ferry.order.core.order.assignmachines;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderAssignMachinesUseCase{
	void execute(OrderAssignMachinesRequest request, OrderAuthPrincipal principal, OrderAssignMachinesPresenter presenter);
}
