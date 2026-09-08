package com.ferry.order.core.service.list;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceListSortBy;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListRequest(String name, ServiceCategory category, Boolean activeOnly, String after,
                                        String before, ServiceListSortBy sortBy, SortDirection sortDir,
                                        Integer pageSize) implements OrderValidation{
}
