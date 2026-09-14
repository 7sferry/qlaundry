package com.ferry.analytics.core.event.laundryservice;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface LaundryServiceEventUseCase{
	void execute(LaundryServiceEventRequest request, LaundryServiceEventPresenter presenter);
}
