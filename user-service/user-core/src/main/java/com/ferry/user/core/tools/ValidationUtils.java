package com.ferry.user.core.tools;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

class ValidationUtils{

	static Validator getValidator(){
		return ValidationHolder.VALIDATOR;
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
