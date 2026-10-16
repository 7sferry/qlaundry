package com.ferry.order.domain.machine;

import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record MachineFilter(String tenantId, String name, MachineType type, boolean activeOnly,
                            MachineListSortBy sortBy, SortDirection sortDir, PageDirection pageDirection,
                            PageCursor cursor, int pageSize){

	public String nameStartsWith(){
		if(name == null || name.isBlank()){
			return null;
		}
		return name.toLowerCase() + '%';
	}

	public Short typeValue(){
		return type == null ? null : type.getValue();
	}

}
