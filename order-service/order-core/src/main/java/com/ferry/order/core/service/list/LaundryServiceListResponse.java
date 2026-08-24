package com.ferry.order.core.service.list;

import com.ferry.order.domain.service.LaundryServiceDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListResponse(List<LaundryServiceDomain> services, String nextCursor, String prevCursor){
}
