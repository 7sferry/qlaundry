package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.staff.constant.LoginConstant;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.PasswordChangedNotifier;
import com.ferry.user.core.tools.PasswordSubject;
import com.ferry.user.core.tools.PasswordSubjectGateway;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.StaffSessionRevoker;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.common.RawPassword;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.PasswordPolicyException;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffPassword;
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
import static org.mockito.ArgumentMatchers.anyString;
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
	private static final String USERNAME_HASH = "351a4cb30764982e6874f44babb5240276f74e23b09f932db830d99cf7fd1b1f";
	private static final String STAFF_ID = "stf-hendra-77";
	private static final String PASSWORD = "ResetSecure2026!";
	private static final String RESET_TOKEN = "ffeeddcc1234";
	private static final String RESET_TOKEN_KEY = PasswordConstant.RESET_TOKEN_KEY + USERNAME;
	private static final PasswordSubject SUBJECT = new PasswordSubject(USERNAME, "Hendra Wijaya",
			"Laundry Sejahtera", "sejahtera_laundry", List.of("hendra.w@mailku.id"), List.of("+6281377001234"));

	@Mock
	StaffResetPasswordGateway gateway;
	@Mock
	PasswordTool passwordTool;
	@Mock
	UserCacheManager cacheManager;
	@Mock
	PasswordSubjectGateway subjectGateway;
	@Mock
	StaffSessionRevoker sessionRevoker;
	@Mock
	PasswordChangedNotifier passwordChangedNotifier;
	@Mock
	AuthAuditLog auditLog;
	@InjectMocks
	DefaultStaffResetPasswordUseCase useCase;
	@Mock
	StaffResetPasswordPresenter presenter;
	@Captor
	ArgumentCaptor<StaffPassword> passwordCaptor;

	@Test
	void givenBlankUsername_thenThrowsFailedToResetPasswordExceptionWithConstraintViolationCause(){
		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(" ", PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_RESET_PASSWORD, AuthResult.FAILURE, " ");
	}

	@Test
	void givenPasswordShorterThanMinimumLength_thenThrowsPasswordPolicyException(){
		PasswordPolicyException thrown = catchThrowableOfType(PasswordPolicyException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, "short12", RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("Password must be at least 8 characters long"));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNoResetTokenCached_thenThrowsFailedToResetPasswordExceptionWithInvalidResetTokenMessage(){
		willReturn(Optional.empty()).given(cacheManager).get(RESET_TOKEN_KEY);

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid reset token"));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenResetTokenMismatch_thenThrowsFailedToResetPasswordExceptionWithInvalidResetTokenMessage(){
		willReturn(Optional.of("different-token")).given(cacheManager).get(RESET_TOKEN_KEY);

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid reset token"));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).getAndDelete(anyString());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameNotFound_thenThrowsFailedToResetPasswordExceptionWithInvalidUsernameMessage(){
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.empty()).given(gateway).findByUsername(new Username(USERNAME));

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid username"));
		then(gateway).should(never()).save(any(StaffPassword.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordSubjectNotFound_thenThrowsFailedToResetPasswordExceptionWithInvalidUsernameMessage(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.empty()).given(subjectGateway).findByStaffId(STAFF_ID);

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid username"));
		then(gateway).should(never()).save(any(StaffPassword.class));
	}

	@Test
	void givenPasswordContainsUsername_thenThrowsPasswordPolicyExceptionAndKeepsResetTokenUsable(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);

		PasswordPolicyException thrown = catchThrowableOfType(PasswordPolicyException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, "My-HendraStaffLogin-1", RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("Password must not contain your username"));
		then(cacheManager).should(never()).getAndDelete(anyString());
		then(gateway).should(never()).save(any(StaffPassword.class));
		then(sessionRevoker).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_RESET_PASSWORD, AuthResult.FAILURE, USERNAME);
	}

	@Test
	void givenPasswordContainsPhoneNumberWithoutCountryCode_thenThrowsPasswordPolicyException(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);

		PasswordPolicyException thrown = catchThrowableOfType(PasswordPolicyException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, "Zx81377001234qq", RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("Password must not contain your phone number"));
		then(gateway).should(never()).save(any(StaffPassword.class));
	}

	@Test
	void givenPasswordWasUsedWithinLastThreeMonths_thenThrowsPasswordPolicyException(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);
		StaffPasswordProjection recentPassword = new StaffPasswordProjection("previously-used-hash");
		willReturn(List.of(recentPassword)).given(gateway).findRecentPasswords(eq(STAFF_ID), any(Instant.class));
		willReturn(true).given(passwordTool).matches(PASSWORD, "previously-used-hash");

		PasswordPolicyException thrown = catchThrowableOfType(PasswordPolicyException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessageContaining("used within the last 3 months"));
		then(cacheManager).should(never()).getAndDelete(anyString());
		then(gateway).should(never()).save(any(StaffPassword.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNewPasswordSameAsCurrentPassword_thenThrowsPasswordPolicyException(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);
		StaffPasswordProjection currentPassword = new StaffPasswordProjection("current-hash");
		willReturn(Optional.of(currentPassword)).given(gateway).findCurrentPassword(STAFF_ID);
		willReturn(true).given(passwordTool).matches(PASSWORD, "current-hash");

		PasswordPolicyException thrown = catchThrowableOfType(PasswordPolicyException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("New password must be different from your current password"));
		then(gateway).should(never()).save(any(StaffPassword.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenResetTokenConsumedByAnotherRequestMeanwhile_thenThrowsFailedToResetPasswordExceptionWithoutSaving(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.empty()).given(cacheManager).getAndDelete(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);
		willReturn(List.of()).given(gateway).findRecentPasswords(eq(STAFF_ID), any(Instant.class));

		FailedToResetPasswordException thrown = catchThrowableOfType(FailedToResetPasswordException.class,
				() -> useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid reset token"));
		then(gateway).should(never()).save(any(StaffPassword.class));
		then(sessionRevoker).shouldHaveNoInteractions();
		then(passwordChangedNotifier).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequestButStaffHasNoEmail_thenResetsPasswordWithoutNotifying(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).getAndDelete(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);
		willReturn(List.of()).given(gateway).findRecentPasswords(eq(STAFF_ID), any(Instant.class));
		willReturn(new HashedPassword("new-hashed-password")).given(passwordTool).hash(new RawPassword(PASSWORD));
		willReturn(Optional.empty()).given(gateway).findEmail(STAFF_ID);

		useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter);

		then(gateway).should().save(any(StaffPassword.class));
		then(passwordChangedNotifier).shouldHaveNoInteractions();
		then(presenter).should().present(new StaffResetPasswordResponse("password has been reset"));
	}

	@Test
	void givenValidRequest_thenHashesPasswordSavesItRevokesSessionsAndPresentsSuccessMessage(){
		Staff existing = Staff.register(new Username(USERNAME), new FullName("Hendra Wijaya"),
						new Description("desc"), "tnt-02", StaffRole.STAFF, null)
				.toBuilder().id(STAFF_ID).build();
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).get(RESET_TOKEN_KEY);
		willReturn(Optional.of(RESET_TOKEN)).given(cacheManager).getAndDelete(RESET_TOKEN_KEY);
		willReturn(Optional.of(existing)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(Optional.of(SUBJECT)).given(subjectGateway).findByStaffId(STAFF_ID);
		willReturn(List.of()).given(gateway).findRecentPasswords(eq(STAFF_ID), any(Instant.class));
		willReturn(new HashedPassword("new-hashed-password")).given(passwordTool).hash(new RawPassword(PASSWORD));
		willReturn(Optional.of("hendra.w@mailku.id")).given(gateway).findEmail(STAFF_ID);

		useCase.execute(new StaffResetPasswordRequest(USERNAME, PASSWORD, RESET_TOKEN), presenter);

		then(gateway).should().save(passwordCaptor.capture());
		then(sessionRevoker).should().revokeAll(STAFF_ID);
		then(passwordChangedNotifier).should().notifyChanged(STAFF_ID, USERNAME, "hendra.w@mailku.id");
		then(cacheManager).should().delete(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		then(cacheManager).should().delete(LoginConstant.OTP_ATTEMPT_KEY + USERNAME_HASH);
		then(auditLog).should().record(AuthAction.STAFF_RESET_PASSWORD, AuthResult.SUCCESS, USERNAME);
		then(presenter).should().present(new StaffResetPasswordResponse("password has been reset"));
		thenSoftly(softly -> {
			softly.then(passwordCaptor.getValue().passwordValue()).isEqualTo("new-hashed-password");
			softly.then(passwordCaptor.getValue().staffId()).isEqualTo(STAFF_ID);
		});
	}

}
