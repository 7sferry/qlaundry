package com.ferry.user.domain.staff;

import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record StaffFilter(String fullName, String tenantId, String username, SortBy sortBy, SortDirection sortDir,
                          PageDirection pageDirection, PageCursor cursor){

	public String fullNameStartsWith(){
		if(fullName == null || fullName.isBlank()){
			return null;
		}
		return fullName.toLowerCase() + '%';
	}

}
