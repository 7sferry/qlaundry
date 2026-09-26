package com.ferry.user.domain.staff;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffId(String value){
	public StaffId{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("StaffId value is null");
		}
	}
}
