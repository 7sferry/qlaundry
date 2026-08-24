package com.ferry.order.core.order.detail;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderDetailUseCase{
	void execute(OrderDetailRequest request, OrderAuthPrincipal principal, OrderDetailPresenter presenter);
}
