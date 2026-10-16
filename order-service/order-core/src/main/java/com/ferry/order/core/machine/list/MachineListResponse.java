package com.ferry.order.core.machine.list;

import com.ferry.order.domain.machine.MachineUsage;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineListResponse(List<MachineUsage> machines, String nextCursor, String prevCursor){
}
