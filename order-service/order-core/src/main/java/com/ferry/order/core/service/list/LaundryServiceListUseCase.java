package com.ferry.order.core.service.list;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceListUseCase{
	void execute(LaundryServiceListRequest request, OrderAuthPrincipal principal, LaundryServiceListPresenter presenter);
}
