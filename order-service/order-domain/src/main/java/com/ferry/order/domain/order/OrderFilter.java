package com.ferry.order.domain.order;

import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderFilter(String tenantId, OrderStatus status, OrderPriority priority, String customerId,
                          String orderNumber, Instant from, Instant to, SortBy sortBy, SortDirection sortDir,
                          PageDirection pageDirection, PageCursor cursor){

	public Short statusValue(){
		return status == null ? null : status.getValue();
	}

	public Short priorityValue(){
		return priority == null ? null : priority.getValue();
	}

	public String orderNumberStartsWith(){
		if(orderNumber == null || orderNumber.isBlank()){
			return null;
		}
		return orderNumber.toUpperCase() + '%';
	}

}
