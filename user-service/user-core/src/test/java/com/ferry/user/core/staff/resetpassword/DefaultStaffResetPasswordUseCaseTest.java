package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.RawPasswordDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.forgottenpassword.FailedToResetPasswordException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
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
class DefaultStaffResetPasswordUseCaseTest{

	private static final String USERNAME = "hendrastafflogin";
	private static final String STAFF_ID = "stf-hendra-77";
	private static final String PASSWORD = "ResetSecure2026!";
	private static final String RESET_TOKEN = "ffeeddcc1234";

	@Mock
	StaffResetPasswordGateway gateway;
	@Mock
	PasswordTool passwordTool;
	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultStaffResetPasswordUseCase useCase;
	@Mock
	StaffResetPasswordPresenter presenter;
	@Captor
	ArgumentCaptor<StaffPasswordDomain> passwordCaptor;

	@Test
	void givenBlankUsername_thenThrowsFailedToResetPasswordExceptionWithConstraintViolationCause(){
		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(" ", PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordShorterThanMinimumLength_thenThrowsFailedToResetPasswordExceptionWithInvalidPasswordCause(){
		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, "short12", RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidPasswordException.class));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNoResetTokenCached_thenThrowsFailedToResetPasswordExceptionWithInvalidResetTokenMessage(){
		willReturn(Optional.empty()).given(cacheManager).getAndDelete(PasswordConstant.RESET_TOKEN_KEY + USERNAME);

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid reset token"));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenResetTokenMismatch_thenThrowsFailedToResetPasswordExceptionWithInvalidResetTokenMessage(){
		willReturn(Optional.of("different-token")).given(cacheManager).getAndDelete(PasswordConstant.RESET_TOKEN_KEY + USERNAME);

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid reset token"));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameNotFound_thenThrowsFailedToResetPasswordExceptionWithInvalidUsernameMessage(){
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).getAndDelete(PasswordConstant.RESET_TOKEN_KEY + USERNAME);
		willReturn(Optional.empty()).given(gateway).findByUsername(new UsernameDomain(USERNAME));

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid username"));
		then(gateway).should(never()).save(any(StaffPasswordDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordWasUsedWithinLastThreeMonths_thenThrowsFailedToResetPasswordExceptionWithInvalidPasswordCause(){
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).getAndDelete(PasswordConstant.RESET_TOKEN_KEY + USERNAME);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME), new FullNameDomain("Hendra Wijaya"),
						new DescriptionDomain("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		StaffPasswordProjection recentPassword = new StaffPasswordProjection("previously-used-hash");
		willReturn(List.of(recentPassword)).given(gateway).findRecentPasswords(eq(STAFF_ID), any(Instant.class));
		willReturn(true).given(passwordTool).matches(PASSWORD, "previously-used-hash");

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidPasswordException.class));
		then(gateway).should(never()).save(any(StaffPasswordDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNewPasswordSameAsCurrentPassword_thenThrowsFailedToResetPasswordExceptionWithInvalidPasswordCause(){
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).getAndDelete(PasswordConstant.RESET_TOKEN_KEY + USERNAME);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME), new FullNameDomain("Hendra Wijaya"),
						new DescriptionDomain("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		StaffPasswordProjection currentPassword = new StaffPasswordProjection("current-hash");
		willReturn(Optional.of(currentPassword)).given(gateway).findCurrentPassword(STAFF_ID);
		willReturn(true).given(passwordTool).matches(PASSWORD, "current-hash");

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidPasswordException.class));
		then(gateway).should(never()).save(any(StaffPasswordDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequest_thenHashesPasswordSavesPasswordHistoryAndPresentsSuccessMessage(){
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).getAndDelete(PasswordConstant.RESET_TOKEN_KEY + USERNAME);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME), new FullNameDomain("Hendra Wijaya"),
						new DescriptionDomain("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		willReturn(List.of()).given(gateway).findRecentPasswords(eq(STAFF_ID), any(Instant.class));
		willReturn(new HashedPasswordDomain("new-hashed-password")).given(passwordTool).hash(new RawPasswordDomain(PASSWORD));

		useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter);

		then(gateway).should().save(passwordCaptor.capture());
		then(presenter).should().present(new StaffResetPasswordResponse("password has been reset"));
		thenSoftly(softly -> {
			softly.then(passwordCaptor.getValue().passwordValue()).isEqualTo("new-hashed-password");
			softly.then(passwordCaptor.getValue().staffId()).isEqualTo(STAFF_ID);
		});
	}

}
