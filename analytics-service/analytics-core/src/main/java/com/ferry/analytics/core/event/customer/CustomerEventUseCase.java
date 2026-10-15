package com.ferry.analytics.core.event.customer;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerEventUseCase{
	void execute(CustomerEventRequest request, CustomerEventPresenter presenter);
}
