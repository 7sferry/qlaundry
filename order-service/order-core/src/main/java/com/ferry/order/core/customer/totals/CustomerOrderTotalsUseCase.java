package com.ferry.order.core.customer.totals;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface CustomerOrderTotalsUseCase{
	void execute(CustomerOrderTotalsRequest request, OrderAuthPrincipal principal, CustomerOrderTotalsPresenter presenter);
}
