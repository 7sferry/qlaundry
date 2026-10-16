package com.ferry.order.core.machine.create;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineCreateUseCase{
	void execute(MachineCreateRequest request, OrderAuthPrincipal principal, MachineCreatePresenter presenter);
}
