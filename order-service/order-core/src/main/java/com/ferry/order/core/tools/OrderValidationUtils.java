package com.ferry.order.core.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

class OrderValidationUtils{

	static <T> Set<ConstraintViolation<T>> validate(T object){
		return ValidationHolder.VALIDATOR.validate(object);
	}

	static class ValidationHolder{
		private static final Validator VALIDATOR;

		static{
			try(ValidatorFactory factory = Validation.byDefaultProvider()
					.configure()
					.messageInterpolator(new ParameterMessageInterpolator())
					.buildValidatorFactory()){
				VALIDATOR = factory.getValidator();
			}
		}
	}
}
