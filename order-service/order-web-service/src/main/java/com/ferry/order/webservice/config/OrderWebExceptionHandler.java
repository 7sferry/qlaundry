package com.ferry.order.webservice.config;

import com.ferry.order.domain.common.exception.*;
import com.ferry.utils.httpclient.HttpClientException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestControllerAdvice
@Slf4j
public class OrderWebExceptionHandler{

	@ExceptionHandler({InvalidOrderStatusException.class, UnsupportedPaymentMethodException.class,
			IllegalArgumentException.class, ConstraintViolationException.class})
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

	@ExceptionHandler(NotFoundException.class)
	ProblemDetail handleNotFoundRequest(RuntimeException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(HttpClientException.class)
	ProblemDetail handleCustomerVerificationError(HttpClientException e){
		log.error(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
				"Customer verification is unavailable. Please try again.");
	}

	@ExceptionHandler(OrderForbiddenActionException.class)
	ProblemDetail handleForbidden(OrderForbiddenActionException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	ProblemDetail handleError(Throwable e){
		log.error(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
	}

}
