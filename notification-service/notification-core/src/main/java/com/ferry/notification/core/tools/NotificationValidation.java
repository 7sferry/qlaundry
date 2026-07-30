package com.ferry.notification.core.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface NotificationValidation{
	default void validate(){
		Set<ConstraintViolation<NotificationValidation>> violations = NotificationValidationUtils.validate(this);
		if(!violations.isEmpty()){
			throw new ConstraintViolationException(violations);
		}
	}
}
