package com.ferry.user.domain.staff;

import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record StaffFilter(String fullName, String tenantId, String username){

	public String fullNameStartsWith(){
		if(fullName == null || fullName.isBlank()){
			return null;
		}
		return fullName.toLowerCase() + '%';
	}

}
