package com.ferry.analytics.core.event.order;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderEventUseCase{
	void execute(OrderEventRequest request, OrderEventPresenter presenter);
}
