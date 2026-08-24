package com.ferry.order.core.service.update;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceUpdateUseCase{
	void execute(LaundryServiceUpdateRequest request, OrderAuthPrincipal principal,
	             LaundryServiceUpdatePresenter presenter);
}
