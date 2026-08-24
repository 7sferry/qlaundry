package com.ferry.user.domain.customer;

import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record CustomerFilter(String fullName, String phone, String tenantId){

	public String fullNameStartsWith(){
		if(fullName == null || fullName.isBlank()){
			return null;
		}
		return fullName.toLowerCase() + '%';
	}

	public boolean hasPhone(){
		return phone != null && !phone.isBlank();
	}

}
