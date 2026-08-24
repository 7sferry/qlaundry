package com.ferry.user.domain.customer;

import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record CustomerFilter(String fullName, String phone, String tenantId, SortBy sortBy, SortDirection sortDir,
                             PageDirection pageDirection, PageCursor cursor){

	public String fullNameStartsWith(){
		if(fullName == null || fullName.isBlank()){
			return null;
		}
		return fullName.toLowerCase() + '%';
	}

	public boolean hasPhone(){
		return phone != null && !phone.isBlank();
	}

}
