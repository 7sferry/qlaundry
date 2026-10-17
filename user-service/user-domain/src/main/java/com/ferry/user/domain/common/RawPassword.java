package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.PasswordPolicyException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record RawPassword(String value){
	public RawPassword {
		if(value == null || value.isBlank()){
			throw new PasswordPolicyException("Password must not be blank");
		}
		if(value.length() < 8){
			throw new PasswordPolicyException("Password must be at least 8 characters long");
		}
	}
}
