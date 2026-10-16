package com.ferry.order.core.machine.list;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineListUseCase{
	void execute(MachineListRequest request, OrderAuthPrincipal principal, MachineListPresenter presenter);
}
