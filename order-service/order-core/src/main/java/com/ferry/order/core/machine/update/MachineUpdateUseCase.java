package com.ferry.order.core.machine.update;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineUpdateUseCase{
	void execute(MachineUpdateRequest request, OrderAuthPrincipal principal, MachineUpdatePresenter presenter);
}
