package com.ferry.user.domain.staff;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffIdDomain(String value){
	public StaffIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("StaffIdDomain value is null");
		}
	}
}
