package com.ferry.user.webservice.config;

import com.ferry.user.domain.exception.ExpiredSessionException;
import com.ferry.user.domain.exception.ForbiddenActionException;
import com.ferry.user.domain.exception.InvalidOtpException;
import com.ferry.user.domain.exception.InvalidUsernameException;
import com.ferry.user.domain.exception.TurnstileVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestControllerAdvice
@Slf4j
public class UserWebExceptionHandler{

	public record ErrorWebResponse(String message){
	}

	@ExceptionHandler(ExpiredSessionException.class)
	ResponseEntity<ErrorWebResponse> handleExpiredSession(ExpiredSessionException e){
		log.warn(e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorWebResponse(e.getMessage()));
	}

	@ExceptionHandler({InvalidOtpException.class, InvalidUsernameException.class, TurnstileVerificationException.class})
	ResponseEntity<ErrorWebResponse> handleBadRequest(RuntimeException e){
		log.warn(e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorWebResponse(e.getMessage()));
	}

	@ExceptionHandler(ForbiddenActionException.class)
	ResponseEntity<ErrorWebResponse> handleForbidden(ForbiddenActionException e){
		log.warn(e.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorWebResponse(e.getMessage()));
	}

}
