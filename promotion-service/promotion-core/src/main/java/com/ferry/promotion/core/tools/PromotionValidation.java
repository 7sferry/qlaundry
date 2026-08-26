package com.ferry.promotion.core.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionValidation{
	default void validate(){
		Set<ConstraintViolation<PromotionValidation>> violations = PromotionValidationUtils.validate(this);
		if(!violations.isEmpty()){
			throw new ConstraintViolationException(violations);
		}
	}
}
