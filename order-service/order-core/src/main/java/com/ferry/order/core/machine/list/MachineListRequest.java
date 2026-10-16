package com.ferry.order.core.machine.list;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.machine.MachineListSortBy;
import com.ferry.order.domain.machine.MachineType;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineListRequest(String name, MachineType type, Boolean activeOnly, String after, String before,
                                 MachineListSortBy sortBy, SortDirection sortDir,
                                 Integer pageSize) implements OrderValidation{
}
