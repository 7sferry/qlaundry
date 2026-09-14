package com.ferry.analytics.domain.report;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ServiceBreakdownProjection(String serviceId, String serviceName, long count, BigDecimal revenue){
}
