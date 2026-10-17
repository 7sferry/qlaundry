package com.ferry.order.core.order.assignstaffs;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderAssignStaffsUseCase{
	void execute(OrderAssignStaffsRequest request, OrderAuthPrincipal principal, OrderAssignStaffsPresenter presenter);
}
