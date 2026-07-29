package com.ferry.user.core.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserValidation{
	default void validate(){
		Set<ConstraintViolation<UserValidation>> violations = ValidationUtils.getValidator().validate(this);
		if(!violations.isEmpty()){
			throw new ConstraintViolationException(violations);
		}
	}
}
