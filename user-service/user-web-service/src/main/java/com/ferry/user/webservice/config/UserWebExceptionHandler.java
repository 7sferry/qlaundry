package com.ferry.user.webservice.config;

import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationResponse;
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
import org.springframework.http.ProblemDetail;
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

	@ExceptionHandler(ExpiredSessionException.class)
	ProblemDetail handleExpiredSession(ExpiredSessionException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler({InvalidUsernameException.class, TurnstileVerificationException.class,
			InvalidPasswordException.class, IllegalArgumentException.class,
			ConstraintViolationException.class})
	ProblemDetail handleBadRequest(RuntimeException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	ProblemDetail handleNotFoundRequest(RuntimeException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(FailedToLoginException.class)
	ProblemDetail handleLoginError(FailedToLoginException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Incorrect username or password");
	}

	@ExceptionHandler(FailedToResetPasswordException.class)
	ProblemDetail handleResetPasswordError(FailedToResetPasswordException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Failed to Reset Password. Please try again.");
	}

	@ExceptionHandler(FailedToSubmitOtpException.class)
	ProblemDetail handleSubmitOtpError(FailedToSubmitOtpException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid OTP.");
	}

	@ExceptionHandler(FailedToConfirmTenantException.class)
	ProblemDetail handleConfirmTenantError(FailedToConfirmTenantException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid or expired confirmation link.");
	}

	@ExceptionHandler(ForbiddenActionException.class)
	ProblemDetail handleForbidden(ForbiddenActionException e){
		log.warn(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	ProblemDetail handleError(Throwable e){
		log.error(e.getMessage(), e);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
	}

}
