package com.ferry.order.core.order.schedule;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderScheduleUseCase{
	void execute(OrderScheduleRequest request, OrderAuthPrincipal principal, OrderSchedulePresenter presenter);
}
