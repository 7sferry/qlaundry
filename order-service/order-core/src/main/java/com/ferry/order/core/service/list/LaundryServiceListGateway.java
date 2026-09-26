package com.ferry.order.core.service.list;

import com.ferry.utils.pagination.CursorFetch;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceFilter;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceListGateway{
	CursorFetch<LaundryService> findByFilter(LaundryServiceFilter filter);
}
