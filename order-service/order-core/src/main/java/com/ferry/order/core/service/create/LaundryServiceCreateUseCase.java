package com.ferry.order.core.service.create;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceCreateUseCase{
	void execute(LaundryServiceCreateRequest request, OrderAuthPrincipal principal,
	             LaundryServiceCreatePresenter presenter);
}
