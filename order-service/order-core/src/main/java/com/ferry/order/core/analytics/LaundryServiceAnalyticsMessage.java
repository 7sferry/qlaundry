package com.ferry.order.core.analytics;

import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record LaundryServiceAnalyticsMessage(
	String tenantId,
	String serviceId,
	String name,
	ServiceCategory category,
	ServiceUnit unit,
	BigDecimal pricePerUnit,
	int estimatedHours,
	double expressMultiplier,
	boolean popular,
	boolean active,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){

	public static LaundryServiceAnalyticsMessage from(LaundryService service){
		return new LaundryServiceAnalyticsMessage(service.tenantId(), service.id(), service.name(), service.category(),
				service.unit(), service.pricePerUnit().value(), service.estimatedHours(), service.expressMultiplier(),
				service.popular(), service.active(), service.deleted(),
				service.version() == null ? 0 : service.version(),
				service.createdAt() == null ? null : service.createdAt().toEpochMilli(),
				service.updatedAt() == null ? null : service.updatedAt().toEpochMilli());
	}

}
