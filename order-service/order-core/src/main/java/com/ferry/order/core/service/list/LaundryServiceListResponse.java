package com.ferry.order.core.service.list;

import com.ferry.order.domain.service.LaundryService;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListResponse(List<LaundryService> services, String nextCursor, String prevCursor){
}
