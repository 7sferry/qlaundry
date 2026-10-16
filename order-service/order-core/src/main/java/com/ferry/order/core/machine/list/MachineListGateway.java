package com.ferry.order.core.machine.list;

import com.ferry.order.domain.machine.MachineFilter;
import com.ferry.order.domain.machine.MachineUsage;
import com.ferry.utils.pagination.CursorFetch;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineListGateway{
	CursorFetch<MachineUsage> findByFilter(MachineFilter filter);
}
