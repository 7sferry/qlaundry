package com.ferry.user.core.staff.login;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.staff.constant.LoginConstant;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tenant.registration.VerificationGateway;
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
import com.ferry.user.domain.staff.login.LoginChallenge;
import com.ferry.user.domain.staff.login.LoginChallengeRequiredException;
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
	private static final String OTP = "482913";
	private static final String CAPTCHA_TOKEN = "turnstile-token-7781";
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
	VerificationGateway verificationGateway;
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
				() -> useCase.execute(new StaffLoginRequest(" ", PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankPassword_thenThrowsFailedToLoginExceptionWithoutLookupOrCounting(){
		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, " ", null, null), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("password required"));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameShorterThanMinimumLength_thenThrowsFailedToLoginExceptionWithInvalidUsernameCause(){
		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest("citr", PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(InvalidUsernameException.class));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenStaffNotFound_thenThrowsFailedToLoginExceptionWithUserIdNotFoundCause(){
		willReturn(Optional.empty()).given(gateway).findByUsername(new Username(USERNAME));

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("userId not found"));
		then(passwordTool).should().simulateMatch(PASSWORD);
		then(passwordTool).should(never()).matches(anyString(), anyString());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordDoesNotMatch_thenThrowsFailedToLoginExceptionWithPasswordNotMatchCause(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), any());

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("password not match"));
		then(passwordTool).should(never()).simulateMatch(anyString());
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
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

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
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

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
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

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

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter);

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

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter);

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

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter);

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

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter);

		thenSoftly(softly -> softly.then(principalCaptor.getValue().timeZone()).isEqualTo(ZoneId.of("UTC")));
	}


	@Test
	void givenCounterBelowCaptchaThreshold_thenWrongPasswordFailsWithoutAChallenge(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(2L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isNull());
		then(verificationGateway).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, USER_ID, USERNAME);
	}

	@Test
	void givenThirdWrongPassword_thenFailureAsksForCaptchaOnTheNextAttempt(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(3L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.CAPTCHA));
	}

	@Test
	void givenSeventhFailure_thenFailureAsksForOtpOnTheNextAttempt(){
		willReturn(Optional.of("6")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(Optional.of(new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue()))).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), anyString());
		willReturn(7L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, CAPTCHA_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.OTP));
	}

	@Test
	void givenCaptchaLevelAndNoToken_thenRequiresCaptchaWithoutLookupOrCounting(){
		willReturn(Optional.of("3")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);

		LoginChallengeRequiredException thrown = catchThrowableOfType(LoginChallengeRequiredException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.CAPTCHA));
		then(gateway).shouldHaveNoInteractions();
		then(passwordTool).shouldHaveNoInteractions();
		then(verificationGateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.CHALLENGED, null, USERNAME);
	}

	@Test
	void givenCaptchaLevelAndRejectedToken_thenRequiresCaptchaWithoutLookupOrCounting(){
		willReturn(Optional.of("5")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(false).given(verificationGateway).verify(CAPTCHA_TOKEN);

		LoginChallengeRequiredException thrown = catchThrowableOfType(LoginChallengeRequiredException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, CAPTCHA_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.CAPTCHA));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
	}

	@Test
	void givenCaptchaLevelAndValidTokenAndCorrectPassword_thenLogsInAndClearsTheCounters(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of("6")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
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

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, CAPTCHA_TOKEN), presenter);

		then(cacheManager).should().delete(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		then(cacheManager).should().delete(LoginConstant.OTP_ATTEMPT_KEY + USERNAME_HASH);
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.SUCCESS, USER_ID, USERNAME);
		then(presenter).should().present(new StaffLoginResponse(ACCESS_TOKEN, REFRESH_TOKEN));
	}

	@Test
	void givenCaptchaLevelOnAnUnknownAccount_thenBehavesLikeAnExistingOne(){
		willReturn(Optional.of("5")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + GHOST_USERNAME_HASH);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willReturn(Optional.empty()).given(gateway).findByUsername(new Username(GHOST_USERNAME));
		willReturn(6L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + GHOST_USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(GHOST_USERNAME, PASSWORD, null, CAPTCHA_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.CAPTCHA));
		then(passwordTool).should().simulateMatch(PASSWORD);
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, null, GHOST_USERNAME);
	}

	@Test
	void givenOtpLevelAndNoOtp_thenRequiresOtpEvenWithTheCorrectPassword(){
		willReturn(Optional.of("7")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);

		LoginChallengeRequiredException thrown = catchThrowableOfType(LoginChallengeRequiredException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, CAPTCHA_TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.OTP));
		then(gateway).shouldHaveNoInteractions();
		then(passwordTool).shouldHaveNoInteractions();
		then(verificationGateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.CHALLENGED, null, USERNAME);
	}

	@Test
	void givenOtpLevelAndWrongOtp_thenCountsBothFailuresAndKeepsAskingForOtp(){
		willReturn(Optional.of("7")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(Optional.of(OTP)).given(cacheManager).get(PasswordConstant.OTP_KEY + USERNAME);
		willReturn(1L).given(cacheManager).increment(LoginConstant.OTP_ATTEMPT_KEY + USERNAME_HASH,
				PasswordConstant.OTP_DURATION);
		willReturn(8L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, null, "000000", null), presenter));

		thenSoftly(softly -> {
			softly.then(thrown.getCause()).isInstanceOf(InvalidPasswordException.class).hasMessage("otp not match");
			softly.then(thrown.challenge()).isEqualTo(LoginChallenge.OTP);
		});
		then(cacheManager).should(never()).delete(PasswordConstant.OTP_KEY + USERNAME);
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenFifthWrongOtp_thenDiscardsTheOtpSoItCannotBeGuessedFurther(){
		willReturn(Optional.of("9")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(Optional.of(OTP)).given(cacheManager).get(PasswordConstant.OTP_KEY + USERNAME);
		willReturn(5L).given(cacheManager).increment(LoginConstant.OTP_ATTEMPT_KEY + USERNAME_HASH,
				PasswordConstant.OTP_DURATION);

		catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, null, "111111", null), presenter));

		then(cacheManager).should().delete(PasswordConstant.OTP_KEY + USERNAME);
		then(cacheManager).should().delete(LoginConstant.OTP_ATTEMPT_KEY + USERNAME_HASH);
	}

	@Test
	void givenOtpLevelOnAnUnknownAccount_thenFailsLikeAWrongOtp(){
		willReturn(Optional.of("7")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + GHOST_USERNAME_HASH);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(GHOST_USERNAME, null, OTP, null), presenter));

		thenSoftly(softly -> {
			softly.then(thrown.getCause()).isInstanceOf(InvalidPasswordException.class).hasMessage("otp not match");
			softly.then(thrown.challenge()).isEqualTo(LoginChallenge.OTP);
		});
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, null, GHOST_USERNAME);
	}

	@Test
	void givenOtpLevelAndCorrectOtp_thenConsumesTheOtpLogsInWithoutAPasswordAndClearsTheCounters(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of("7")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(Optional.of(OTP)).given(cacheManager).get(PasswordConstant.OTP_KEY + USERNAME);
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSession.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan", ZoneId.of("Asia/Jakarta"), TenantStatus.ACTIVE.getValue())))
				.given(gateway).findTenantById(new TenantId(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any(UserAuthPrincipal.class));
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, null, OTP, null), presenter);

		then(cacheManager).should().delete(PasswordConstant.OTP_KEY + USERNAME);
		then(cacheManager).should().delete(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		then(cacheManager).should().delete(LoginConstant.OTP_ATTEMPT_KEY + USERNAME_HASH);
		then(passwordTool).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.SUCCESS, USER_ID, USERNAME);
		then(presenter).should().present(new StaffLoginResponse(ACCESS_TOKEN, REFRESH_TOKEN));
	}

	@Test
	void givenOtpButCounterBelowOtpLevel_thenOtpIsIgnoredAndMissingPasswordIsNotCounted(){
		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, null, OTP, null), presenter));

		thenSoftly(softly -> softly.then(thrown).hasMessage("password required"));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(cacheManager).should(never()).delete(PasswordConstant.OTP_KEY + USERNAME);
	}

	@Test
	void givenUnreadableCounter_thenTreatsItAsZero(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of("not-a-number")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new Username(USERNAME));
		willReturn(false).given(passwordTool).matches(eq(PASSWORD), anyString());

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isNull());
		then(verificationGateway).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameWithDifferentCaseAndPadding_thenUsesTheSameCounterAsTheNormalizedUsername(){
		willReturn(Optional.of("5")).given(cacheManager).get(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);

		LoginChallengeRequiredException thrown = catchThrowableOfType(LoginChallengeRequiredException.class,
				() -> useCase.execute(new StaffLoginRequest("  CitraAdminStaff ", PASSWORD, null, null), presenter));

		thenSoftly(softly -> softly.then(thrown.challenge()).isEqualTo(LoginChallenge.CAPTCHA));
		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameLongerThanFiftyCharacters_thenRejectsWithoutLookupAndCountsTheAttempt(){
		willReturn(1L).given(cacheManager).increment(LoginConstant.ATTEMPT_KEY + OVERSIZED_USERNAME_HASH,
				LoginConstant.ATTEMPT_WINDOW);

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(OVERSIZED_USERNAME, PASSWORD, null, null), presenter));

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
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter));

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

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD, null, null), presenter);

		then(cacheManager).should().delete(LoginConstant.ATTEMPT_KEY + USERNAME_HASH);
		then(cacheManager).should(never()).increment(anyString(), any(Duration.class));
		then(auditLog).should().record(AuthAction.STAFF_LOGIN, AuthResult.SUCCESS, USER_ID, USERNAME);
	}

}
