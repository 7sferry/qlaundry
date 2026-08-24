package com.ferry.order.core.service.list;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.order.domain.service.ServiceCategory;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListRequest(String name, ServiceCategory category, Boolean activeOnly, String cursor,
                                        PageDirection direction, SortBy sortBy,
                                        SortDirection sortDir) implements OrderValidation{
}
