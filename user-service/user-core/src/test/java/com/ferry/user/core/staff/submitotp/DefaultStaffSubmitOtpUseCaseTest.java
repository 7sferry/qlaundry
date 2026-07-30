package com.ferry.user.core.staff.submitotp;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.staff.submitotp.FailedToSubmitOtpException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffSubmitOtpUseCaseTest{

	private static final String USERNAME = "sitiotpuser";
	private static final String OTP = "998877";

	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultStaffSubmitOtpUseCase useCase;
	@Mock
	StaffSubmitOtpPresenter presenter;
	@Captor
	ArgumentCaptor<String> resetTokenCaptor;

	@Test
	void givenBlankUsername_thenThrowsFailedToSubmitOtpExceptionWithConstraintViolationCause(){
		FailedToSubmitOtpException thrown = catchThrowableOfType(FailedToSubmitOtpException.class,
				() -> useCase.execute(new StaffSubmitOtpRequest(" ", OTP), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankOtp_thenThrowsFailedToSubmitOtpExceptionWithConstraintViolationCause(){
		FailedToSubmitOtpException thrown = catchThrowableOfType(FailedToSubmitOtpException.class,
				() -> useCase.execute(new StaffSubmitOtpRequest(USERNAME, " "), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNoOtpCached_thenThrowsFailedToSubmitOtpExceptionWithInvalidOtpMessage(){
		willReturn(Optional.empty()).given(cacheManager).get(PasswordConstant.OTP_KEY + USERNAME);

		FailedToSubmitOtpException thrown = catchThrowableOfType(FailedToSubmitOtpException.class,
				() -> useCase.execute(new StaffSubmitOtpRequest(USERNAME, OTP), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid otp"));
		then(cacheManager).should(never()).delete(any());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenOtpMismatch_thenThrowsFailedToSubmitOtpExceptionWithInvalidOtpMessage(){
		willReturn(Optional.of("112233")).given(cacheManager).get(PasswordConstant.OTP_KEY + USERNAME);

		FailedToSubmitOtpException thrown = catchThrowableOfType(FailedToSubmitOtpException.class,
				() -> useCase.execute(new StaffSubmitOtpRequest(USERNAME, OTP), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid otp"));
		then(cacheManager).should(never()).delete(any());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidOtp_thenDeletesOtpGeneratesResetTokenAndPresentsResponse(){
		willReturn(Optional.of(OTP)).given(cacheManager).get(PasswordConstant.OTP_KEY + USERNAME);

		useCase.execute(new StaffSubmitOtpRequest(USERNAME, OTP), presenter);

		then(cacheManager).should().delete(PasswordConstant.OTP_KEY + USERNAME);
		then(cacheManager).should().set(eq(PasswordConstant.RESET_TOKEN_KEY + USERNAME), resetTokenCaptor.capture(),
				eq(PasswordConstant.RESET_TOKEN_DURATION));
		then(presenter).should().present(new StaffSubmitOtpResponse(resetTokenCaptor.getValue()));

		thenSoftly(softly -> {
			softly.then(resetTokenCaptor.getValue()).isNotBlank();
			softly.then(resetTokenCaptor.getValue()).matches("^[0-9a-f]{64}$");
		});
	}

}
