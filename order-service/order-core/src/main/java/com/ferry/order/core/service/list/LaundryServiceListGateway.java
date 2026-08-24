package com.ferry.order.core.service.list;

import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceFilter;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceListGateway{
	List<LaundryServiceDomain> findByFilter(LaundryServiceFilter filter);
}
