package com.ferry.order.webservice.service.list;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceListWebResponse(List<Service> services){

	public record Service(String id, String name, String description, BigDecimal pricePerUnit, String unit, String category,
	                      int estimatedHours, double expressMultiplier, boolean popular, boolean active){

	}

}
