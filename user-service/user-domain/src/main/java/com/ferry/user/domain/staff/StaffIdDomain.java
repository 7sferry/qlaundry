package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffIdDomain(String value){
	public StaffIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("StaffIdDomain value is null");
		}
	}
}
