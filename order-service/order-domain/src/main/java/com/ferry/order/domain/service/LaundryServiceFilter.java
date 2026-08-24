package com.ferry.order.domain.service;

import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record LaundryServiceFilter(String tenantId, String name, ServiceCategory category, boolean activeOnly){

	public String nameStartsWith(){
		if(name == null || name.isBlank()){
			return null;
		}
		return name.toLowerCase() + '%';
	}

	public Short categoryValue(){
		return category == null ? null : category.getValue();
	}

}
