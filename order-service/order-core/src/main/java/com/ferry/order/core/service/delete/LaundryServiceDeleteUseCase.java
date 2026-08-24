package com.ferry.order.core.service.delete;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceDeleteUseCase{
	void execute(LaundryServiceDeleteRequest request, OrderAuthPrincipal principal,
	             LaundryServiceDeletePresenter presenter);
}
