package com.ferry.user.core.staff.login;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.staff.constant.LoginConstant;
import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.FailedToLoginException;
import com.ferry.user.domain.staff.login.LoginLockedException;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.domain.token.UserAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffLoginUseCaseTest{

	private static final String USERNAME = "citraadminstaff";
	private static final String USERNAME_HASH = "fa32081e6276a44a5f91199f949f1f90175f4aa5938086a6d1abf9dc5853b927";
	private static final String GHOST_USERNAME = "ghostuser01";
	private static final String OVERSIZED_USERNAME = "a".repeat(51);
	private static final String OVERSIZED_USERNAME_HASH = "bfc5fe0e360152ca98c50fab4ed7e3078c17debc2917740d5000913b686ca129";
	private static final String GHOST_USERNAME_HASH ="c6907143339f30a3c7684fadf34be5f3126b429795e716a7cbc8a225dc2c4b5c";
	private static final String PASSWORD = "Passw0rd!2026";
	private static final String USER_ID = "usr-445566";
	private static final String TENANT_ID = "tnt-medan-04";
	private static final String HASHED_PASSWORD = "citra-hashed-pw";
	private static final String FULL_NAME = "Citra Wulandari";
	private static final String REFRESH_TOKEN = "login-rt-raw-9988";
	private static final String HASHED_REFRESH_TOKEN = "login-rt-hash-9988";
	private static final String ACCESS_TOKEN = "login-access-token-556";

	@Mock
	StaffLoginGateway gateway;
	@Mock
	PasswordTool passwordTool;
	@Mock
	TokenProcessor tokenProcessor;
	@Mock
	UserCacheManager cacheManager;
	@Mock
	AuthAuditLog auditLog;
	@InjectMocks
	DefaultStaffLoginUseCase useCase;
	@Mock
	StaffLoginPresenter presenter;
	@Captor
	ArgumentCaptor<UserAuthPrincipal> principalCaptor;

	@Test
	void givenBlankUsername_thenThrowsFailedToLoginExceptionWithConstraintViolationCause(){
		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(" ", PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankPassword_thenThrowsFailedToLoginExceptionWithConstraintViolationCause(){
		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, " "), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameShorterThanMinimumLength_thenThrowsFailedToLoginExceptionWithInvalidUsernameCause(){
		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest("citr", PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidUsernameException.class));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenStaffNotFound_thenThrowsFailedToLoginExceptionWithUserIdNotFoundCause(){
		willReturn(Optional.empty()).given(gateway).findByUsername(new Username(USERNAME));

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("userId not found"));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordDoesNotMatch_thenThrowsFailedToLoginExceptionWithPasswordNotMatchCause(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), any());

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("password not match"));
		then(gateway).should(never()).save(any());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantNotFound_thenThrowsFailedToLoginExceptionWithTenantNotFoundCause(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(Optional.empty()).given(gateway).findTenantById(new TenantId(TENANT_ID));

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(NotFoundException.class)
				.hasMessage("tenant not found"));
		then(gateway).should(never()).save(any(UserSession.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantNotConfirmed_thenThrowsFailedToLoginExceptionWithTenantNotConfirmedMessage(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.PENDING.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("tenant not confirmed"));
		then(gateway).should(never()).save(any(UserSession.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenRoleNotFound_thenThrowsFailedToLoginExceptionWithRoleNotFoundCause(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, (short) 99);
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(NotFoundException.class)
				.hasMessage("role not found"));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidCredentials_thenLogsInSuccessfullyAndPresentsTokens(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		then(cacheManager).should().set(eq(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN), any(UserSession.class),
				eq(Duration.ofSeconds(TokenConstant.REFRESH_CACHE_MAX_SECONDS)));
		then(cacheManager).should().set(eq(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN), eq(ACCESS_TOKEN),
				eq(Duration.ofSeconds(855)));
		then(presenter).should().present(new StaffLoginResponse(ACCESS_TOKEN, REFRESH_TOKEN));
	}

	@Test
	void givenAccessTokenCacheDurationNotPositive_thenSkipsCachingAccessTokenButStillPresents(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(30L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		then(cacheManager).should(never()).set(eq(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN), any(), any());
		then(presenter).should().present(new StaffLoginResponse(ACCESS_TOKEN, REFRESH_TOKEN));
	}

	@Test
	void givenTenantWithItsOwnTimeZone_thenAccessTokenCarriesTheTenantTimeZone(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.SUPER_STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Makassar"), TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(principalCaptor.capture());
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		UserAuthPrincipal principal = principalCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(principal.timeZone()).isEqualTo(ZoneId.of("Asia/Makassar"));
			softly.then(principal.tenantId()).isEqualTo(TENANT_ID);
			softly.then(principal.tenantName()).isEqualTo("Tenant Medan");
		});
	}

	@Test
	void givenTenantWithoutTimeZoneYet_thenAccessTokenFallsBackToUtc(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", null, TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(principalCaptor.capture());
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		thenSoftly(softly -> softly.then(principalCaptor.getValue().timeZone()).isEqualTo(ZoneId.of("UTC")));
	}

	@Test
	void givenAccountIsLocked_thenThrowsLoginLockedExceptionWithRemainingTimeAndSkipsLookup(){
		willReturn(Optional.of(Instant.now().plus(Duration.ofMinutes(4)).toString()))
				.given(cacheManager).get(LoginConstant.LOCK_KEY + USERNAME_HASH);

		LoginLockedException thrown = catchThrowableOfType(LoginLockedException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> {
			softly.then(thrown.retryAfter()).isGreaterThan(Duration.ofMinutes(3));
			softly.then(thrown.retryAfter()).isLessThanOrEqualTo(Duration.ofMinutes(4));
		});
		then(gateway).shouldHaveNoInteractions();
		then(passwordTool).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.LOCKED, null, USERNAME);
	}

	@Test
	void givenUnknownAccountIsLocked_thenThrowsLoginLockedExceptionLikeAnExistingOne(){
		willReturn(Optional.of(Instant.now().plus(Duration.ofMinutes(9)).toString()))
				.given(cacheManager).get(LoginConstant.LOCK_KEY + GHOST_USERNAME_HASH);

		LoginLockedException thrown = catchThrowableOfType(LoginLockedException.class,
				() -> useCase.execute(new StaffLoginRequest(GHOST_USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.retryAfter()).isGreaterThan(Duration.ofMinutes(8)));
		then(gateway).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.LOCKED, null, GHOST_USERNAME);
	}

	@Test
	void givenLockExpiryIsUnreadable_thenFallsBackToTheFullLockDuration(){
		willReturn(Optional.of("not-an-instant")).given(cacheManager).get(LoginConstant.LOCK_KEY + USERNAME_HASH);

		LoginLockedException thrown = catchThrowableOfType(LoginLockedException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.retryAfter()).isEqualTo(LoginConstant.LOCK_DURATION));
	}

	@Test
	void givenUsernameWithDifferentCaseAndPadding_thenUsesTheSameLockAsTheNormalizedUsername(){
		willReturn(Optional.of(Instant.now().plus(Duration.ofMinutes(2)).toString()))
				.given(cacheManager).get(LoginConstant.LOCK_KEY + USERNAME_HASH);

		LoginLockedException thrown = catchThrowableOfType(LoginLockedException.class,
				() -> useCase.execute(new StaffLoginRequest("  CitraAdminStaff ", PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown).isNotNull());
		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenWrongPasswordBelowTheLimit_thenCountsTheAttemptWithoutLocking(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(2L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		then(cacheManager).should(never()).set(eq(LoginConstant.LOCK_KEY + USERNAME_HASH), anyString(), any(Duration.class));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, USER_ID, USERNAME);
	}

	@Test
	void givenThirdWrongPasswordInTheWindow_thenLocksTheAccountForTenMinutes(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(3L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidPasswordException.class));
		then(cacheManager).should().set(eq(LoginConstant.LOCK_KEY + USERNAME_HASH), anyString(),
				eq(Duration.ofMinutes(10)));
		then(cacheManager).should().delete(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
	}

	@Test
	void givenThirdAttemptOnAnUnknownAccount_thenLocksItJustLikeAnExistingOne(){
		willReturn(Optional.empty()).given(gateway).findByUsername(new Username(GHOST_USERNAME));
		willReturn(3L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + GHOST_USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(GHOST_USERNAME, PASSWORD), presenter));

		then(cacheManager).should().set(eq(LoginConstant.LOCK_KEY + GHOST_USERNAME_HASH), anyString(),
				eq(LoginConstant.LOCK_DURATION));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, null, GHOST_USERNAME);
	}

	@Test
	void givenUsernameLongerThanFiftyCharacters_thenRejectsWithoutLookupAndCountsTheAttempt(){
		willReturn(1L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + OVERSIZED_USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(OVERSIZED_USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidUsernameException.class));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).should().increment(LoginConstant.ATTEMPT_KEY + OVERSIZED_USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);
	}

	@Test
	void givenTenantNotConfirmed_thenDoesNotCountTheAttempt(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.PENDING.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));

		catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, USER_ID, USERNAME);
	}

	@Test
	void givenSuccessfulLogin_thenClearsFailedAttemptsAndAuditsTheUser(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any(UserAuthPrincipal.class));
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		then(cacheManager).should().delete(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.SUCCESS, USER_ID, USERNAME);
	}

}
