package com.ferry.order.core.machine.list;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineFilter;
import com.ferry.utils.pagination.CursorFetch;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineListGateway{
	CursorFetch<Machine> findByFilter(MachineFilter filter);

	Map<String,String> getCurrentOrderNumberByMachineId(List<Machine> machines);
}
