package com.ferry.user.domain.staff;

import com.ferry.user.domain.tenant.TenantIdDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffListFilter(String fullName, TenantIdDomain tenantId){

	public String fullNameLike(){
		if(fullName == null || fullName.isBlank()){
			return null;
		}
		return "%" + fullName.toLowerCase() + "%";
	}

}
