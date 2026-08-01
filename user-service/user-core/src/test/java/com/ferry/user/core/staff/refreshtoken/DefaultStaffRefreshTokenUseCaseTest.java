package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.staff.refresh.ExpiredSessionException;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
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
import java.util.Optional;

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
class DefaultStaffRefreshTokenUseCaseTest{

	private static final String REFRESH_TOKEN = "rt-raw-556677";
	private static final String HASHED_REFRESH_TOKEN = "rt-hash-556677";
	private static final String USER_ID = "usr-778899";
	private static final String TENANT_ID = "tnt-surabaya-03";
	private static final String USERNAME = "eddyrefreshstaff";
	private static final String FULL_NAME = "Eddy Kurniawan";
	private static final String NEW_REFRESH_TOKEN = "rt-raw-newone";
	private static final String NEW_HASHED_REFRESH_TOKEN = "rt-hash-newone";
	private static final String NEW_ACCESS_TOKEN = "new-access-token";

	@Mock
	StaffRefreshTokenGateway gateway;
	@Mock
	TokenProcessor tokenProcessor;
	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultStaffRefreshTokenUseCase useCase;
	@Mock
	StaffRefreshTokenPresenter presenter;
	@Captor
	ArgumentCaptor<Duration> durationCaptor;
	@Captor
	ArgumentCaptor<StaffRefreshTokenResponse> responseCaptor;

	@Test
	void givenBlankRefreshToken_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(" "), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(tokenProcessor).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenRotatedResponseCached_thenPresentsCachedResponseDirectly(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		StaffRefreshTokenResponse cached = new StaffRefreshTokenResponse("cached-access", "cached-refresh");
		willReturn(Optional.of(cached)).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);

		useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter);

		then(presenter).should().present(cached);
		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenSessionNotFoundAnywhere_thenThrowsExpiredSessionException(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter))
				.isInstanceOf(ExpiredSessionException.class)
				.hasMessage("session expired"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenSessionTypeIsNotStaff_thenThrowsExpiredSessionException(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.CUSTOMER);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter))
				.isInstanceOf(ExpiredSessionException.class)
				.hasMessage("session expired"));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenSessionExpired_thenThrowsExpiredSessionException(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().minusSeconds(60), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter))
				.isInstanceOf(ExpiredSessionException.class)
				.hasMessage("session expired"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenSessionFetchedFromGateway_thenCachesSessionWithCappedDuration(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		UserSessionDomain fetchedSession = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(7200), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(fetchedSession)).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);
		willReturn(Optional.of("cached-access-token")).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);

		useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter);

		then(cacheManager).should().set(eq(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN), eq(fetchedSession), durationCaptor.capture());
		then(presenter).should().present(new StaffRefreshTokenResponse("cached-access-token", null));

		thenSoftly(softly -> softly.then(durationCaptor.getValue()).isEqualTo(Duration.ofSeconds(TokenConstant.REFRESH_CACHE_MAX_SECONDS)));
	}

	@Test
	void givenValidSessionWithCachedAccessToken_thenPresentsCachedAccessTokenWithoutRotation(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.of("cached-access-token")).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);

		useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter);

		then(presenter).should().present(new StaffRefreshTokenResponse("cached-access-token", null));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).should(never()).set(eq(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN), any(), any());
	}

	@Test
	void givenValidSessionWithoutCachedAccessToken_andNotInRotationWindow_thenGeneratesAndCachesNewAccessToken(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		willReturn(Optional.of(new StaffLoginProjection(USER_ID, USERNAME, "hashed-pw", FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue())))
				.given(gateway).findById(USER_ID);
		willReturn(Optional.of(new TenantLoginProjection("Tenant Surabaya", TenantStatus.ACTIVE.getValue()))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));
		willReturn(NEW_ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(60L).given(tokenProcessor).getRotationDurationBeforeExpireInSeconds();
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter);

		then(presenter).should().present(new StaffRefreshTokenResponse(NEW_ACCESS_TOKEN, null));
		then(cacheManager).should().set(eq(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN), eq(NEW_ACCESS_TOKEN), eq(Duration.ofSeconds(840)));
		then(cacheManager).should(never()).set(eq(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN), any(), any());
		then(gateway).should(never()).save(any());
	}

	@Test
	void givenAccessTokenCacheDurationNotPositive_thenSkipsCachingAccessToken(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		willReturn(Optional.of(new StaffLoginProjection(USER_ID, USERNAME, "hashed-pw", FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue())))
				.given(gateway).findById(USER_ID);
		willReturn(Optional.of(new TenantLoginProjection("Tenant Surabaya", TenantStatus.ACTIVE.getValue()))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));
		willReturn(NEW_ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(60L).given(tokenProcessor).getRotationDurationBeforeExpireInSeconds();
		willReturn(30L).given(tokenProcessor).getAccessDurationInSeconds();

		useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter);

		then(presenter).should().present(new StaffRefreshTokenResponse(NEW_ACCESS_TOKEN, null));
		then(cacheManager).should(never()).set(eq(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN), any(), any());
	}

	@Test
	void givenStaffNotFoundDuringAccessTokenGeneration_thenThrowsNotFoundException(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		willReturn(Optional.empty()).given(gateway).findById(USER_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("userId not found"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantNotFoundDuringAccessTokenGeneration_thenThrowsNotFoundException(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		willReturn(Optional.of(new StaffLoginProjection(USER_ID, USERNAME, "hashed-pw", FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue())))
				.given(gateway).findById(USER_ID);
		willReturn(Optional.empty()).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("tenant not found"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenRoleNotFoundDuringAccessTokenGeneration_thenThrowsNotFoundException(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		willReturn(Optional.of(new StaffLoginProjection(USER_ID, USERNAME, "hashed-pw", FULL_NAME, TENANT_ID, (short) 99)))
				.given(gateway).findById(USER_ID);
		willReturn(Optional.of(new TenantLoginProjection("Tenant Surabaya", TenantStatus.ACTIVE.getValue()))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("role not found"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenSessionInRotationWindow_thenRotatesRefreshTokenAndPresentsBothTokens(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(NEW_HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(NEW_REFRESH_TOKEN);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN, StaffRefreshTokenResponse.class);
		UserSessionDomain currentSession = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(30), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(currentSession)).given(cacheManager).get(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN, UserSessionDomain.class);
		willReturn(Optional.empty()).given(cacheManager).get(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		willReturn(Optional.of(new StaffLoginProjection(USER_ID, USERNAME, "hashed-pw", FULL_NAME, TENANT_ID, StaffRole.STAFF.getValue())))
				.given(gateway).findById(USER_ID);
		willReturn(Optional.of(new TenantLoginProjection("Tenant Surabaya", TenantStatus.ACTIVE.getValue()))).given(gateway).findTenantById(new TenantIdDomain(TENANT_ID));
		willReturn(NEW_ACCESS_TOKEN).given(tokenProcessor).generateAccessToken(any());
		willReturn(60L).given(tokenProcessor).getRotationDurationBeforeExpireInSeconds();
		willReturn(900L).given(tokenProcessor).getAccessDurationInSeconds();
		willReturn(Optional.empty()).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(UserSessionDomain.class));
		willReturn(NEW_REFRESH_TOKEN).given(tokenProcessor).generateRefreshToken();
		willReturn(86400L).given(tokenProcessor).getRefreshDurationInSeconds();

		useCase.execute(new StaffRefreshTokenRequest(REFRESH_TOKEN), presenter);

		then(cacheManager).should().delete(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN);
		then(gateway).should(times(2)).save(any(UserSessionDomain.class));
		then(cacheManager).should().set(eq(TokenConstant.REFRESH_KEY + NEW_HASHED_REFRESH_TOKEN), any(UserSessionDomain.class),
				eq(Duration.ofSeconds(TokenConstant.REFRESH_CACHE_MAX_SECONDS)));
		then(cacheManager).should().set(eq(TokenConstant.ACCESS_KEY + NEW_HASHED_REFRESH_TOKEN), eq(NEW_ACCESS_TOKEN),
				eq(Duration.ofSeconds(840)));
		then(cacheManager).should().set(eq(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN), responseCaptor.capture(),
				eq(Duration.ofSeconds(TokenConstant.ROTATION_GRACE_SECONDS)));
		then(presenter).should().present(responseCaptor.getValue());

		StaffRefreshTokenResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
			softly.then(response.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
		});
	}

}
