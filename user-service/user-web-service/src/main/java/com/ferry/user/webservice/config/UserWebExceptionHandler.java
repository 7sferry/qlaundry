package com.ferry.user.webservice.config;

import com.ferry.user.domain.common.exception.*;
import com.ferry.user.domain.staff.forgottenpassword.FailedToResetPasswordException;
import com.ferry.user.domain.staff.login.FailedToLoginException;
import com.ferry.user.domain.staff.refresh.ExpiredSessionException;
import com.ferry.user.domain.staff.registration.TurnstileVerificationException;
import com.ferry.user.domain.staff.submitotp.FailedToSubmitOtpException;
import com.ferry.user.domain.tenant.confirmregistration.FailedToConfirmTenantException;
import com.ferry.user.domain.tenant.resendconfirmation.FailedToResendConfirmationException;
import jakarta.validation.ConstraintViolationException;
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
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorWebResponse(e.getMessage()));
	}

	@ExceptionHandler({InvalidUsernameException.class, TurnstileVerificationException.class,
			InvalidPasswordException.class, IllegalArgumentException.class,
			NotFoundException.class})
	ResponseEntity<ErrorWebResponse> handleBadRequest(RuntimeException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorWebResponse(e.getMessage()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<ErrorWebResponse> handleInvalidRequest(ConstraintViolationException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorWebResponse(e.getMessage()));
	}

	@ExceptionHandler(FailedToLoginException.class)
	ResponseEntity<ErrorWebResponse> handleLoginError(FailedToLoginException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorWebResponse("Incorrect username or password"));
	}

	@ExceptionHandler(FailedToResetPasswordException.class)
	ResponseEntity<ErrorWebResponse> handleResetPasswordError(FailedToResetPasswordException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorWebResponse("Failed to Reset Password. Please try again."));
	}

	@ExceptionHandler(FailedToSubmitOtpException.class)
	ResponseEntity<ErrorWebResponse> handleSubmitOtpError(FailedToSubmitOtpException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorWebResponse("Invalid OTP."));
	}

	@ExceptionHandler(FailedToConfirmTenantException.class)
	ResponseEntity<ErrorWebResponse> handleConfirmTenantError(FailedToConfirmTenantException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorWebResponse("Invalid or expired confirmation link."));
	}

	@ExceptionHandler(FailedToResendConfirmationException.class)
	ResponseEntity<ErrorWebResponse> handleResendConfirmationError(FailedToResendConfirmationException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.ok(new ErrorWebResponse("A new confirmation email has been sent."));
	}

	@ExceptionHandler(ForbiddenActionException.class)
	ResponseEntity<ErrorWebResponse> handleForbidden(ForbiddenActionException e){
		log.warn(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorWebResponse(e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorWebResponse> handleError(Exception e){
		log.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorWebResponse("Internal Error"));
	}

}
