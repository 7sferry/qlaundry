package com.ferry.order.core.order.list;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderListRequest(OrderStatus status, OrderPriority priority, String customerId, String orderNumber,
                               Long from, Long to, String cursor, PageDirection direction, SortBy sortBy,
                               SortDirection sortDir) implements OrderValidation{
}
