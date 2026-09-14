package com.ferry.analytics.core.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsValidation{
	default void validate(){
		Set<ConstraintViolation<AnalyticsValidation>> violations = AnalyticsValidationUtils.validate(this);
		if(!violations.isEmpty()){
			throw new ConstraintViolationException(violations);
		}
	}
}
