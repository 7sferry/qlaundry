package com.ferry.analytics.webservice.config;

import com.ferry.analytics.domain.common.exception.AnalyticsStoreException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RestControllerAdvice
@Slf4j
public class AnalyticsWebExceptionHandler{

	@ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
	ProblemDetail handleBadRequest(RuntimeException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ProblemDetail handleUnreadableBody(HttpMessageNotReadableException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Request body is malformed or carries a value outside the accepted set");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ProblemDetail handleParameterMismatch(MethodArgumentTypeMismatchException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Parameter " + e.getName() + " carries a value outside the accepted set");
	}

	@ExceptionHandler(BindException.class)
	ProblemDetail handleBinding(BindException e){
		log.warn(e.getMessage(), e);
		String field = e.getFieldError() == null ? "request" : e.getFieldError().getField();
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Parameter " + field + " carries a value outside the accepted set");
	}

	@ExceptionHandler(AnalyticsStoreException.class)
	ProblemDetail handleStoreUnavailable(AnalyticsStoreException e){
		log.error(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
				"Analytics are unavailable. Please try again.");
	}

	@ExceptionHandler(Throwable.class)
	ProblemDetail handleError(Throwable e){
		log.error(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
	}

}
