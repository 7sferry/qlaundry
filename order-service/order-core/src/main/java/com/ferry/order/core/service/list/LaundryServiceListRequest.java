package com.ferry.order.core.service.list;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.service.ServiceCategory;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListRequest(String name, ServiceCategory category,
                                        Boolean activeOnly) implements OrderValidation{
}
