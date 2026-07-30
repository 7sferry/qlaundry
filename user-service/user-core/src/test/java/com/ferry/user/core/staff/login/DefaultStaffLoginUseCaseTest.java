package com.ferry.user.core.staff.login;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.FailedToLoginException;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
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
	@InjectMocks
	DefaultStaffLoginUseCase useCase;
	@Mock
	StaffLoginPresenter presenter;

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
		willReturn(Optional.empty()).given(gateway).findByUsername(new UsernameDomain(USERNAME));

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
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
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
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSessionDomain.class));
		willReturn(Optional.empty()).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));

		FailedToLoginException thrown = catchThrowableOfType(FailedToLoginException.class,
				() -> useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause())
				.isInstanceOf(NotFoundException.class)
				.hasMessage("tenant not found"));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenRoleNotFound_thenThrowsFailedToLoginExceptionWithRoleNotFoundCause(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, (short) 99);
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSessionDomain.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan"))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));

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
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSessionDomain.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan"))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		then(cacheManager).should().set(eq(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN), any(UserSessionDomain.class),
				eq(Duration.ofSeconds(TokenConstant.REFRESH_CACHE_MAX_SECONDS)));
		then(cacheManager).should().set(eq(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN), eq(ACCESS_TOKEN),
				eq(Duration.ofSeconds(840)));
		then(presenter).should().present(new StaffLoginResponse(ACCESS_TOKEN, REFRESH_TOKEN));
	}

	@Test
	void givenAccessTokenCacheDurationNotPositive_thenSkipsCachingAccessTokenButStillPresents(){
		StaffLoginProjection staff = new StaffLoginProjection(USER_ID, USERNAME, HASHED_PASSWORD,
				FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue());
		willReturn(Optional.of(staff)).given(gateway).findByUsername(new UsernameDomain(USERNAME));
		willReturn(true).given(passwordTool).matches(eq(PASSWORD), any());
		willReturn(REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSessionDomain.class));
		willReturn(Optional.of(new TenantLoginProjection("Tenant Medan"))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));
		willReturn(ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(30L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffLoginRequest(USERNAME, PASSWORD), presenter);

		then(cacheManager).should(never()).set(eq(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN), any(), any());
		then(presenter).should().present(new StaffLoginResponse(ACCESS_TOKEN, REFRESH_TOKEN));
	}

}
