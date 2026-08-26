package com.ferry.order.core.service.list;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceListSortBy;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListRequest(String name, ServiceCategory category, Boolean activeOnly, String cursor,
                                        PageDirection direction, ServiceListSortBy sortBy,
                                        SortDirection sortDir) implements OrderValidation{
}
