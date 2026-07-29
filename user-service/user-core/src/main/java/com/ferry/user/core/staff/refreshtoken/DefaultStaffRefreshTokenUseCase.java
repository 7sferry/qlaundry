package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.staff.refresh.ExpiredSessionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffRefreshTokenUseCase implements StaffRefreshTokenUseCase{
	private final StaffRefreshTokenGateway gateway;
	private final TokenProcessor tokenProcessor;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(StaffRefreshTokenRequest request, StaffRefreshTokenPresenter presenter){
		request.validate();
		String oldRefreshToken = request.refreshToken();
		String oldHashedRefreshToken = tokenProcessor.hashToken(oldRefreshToken);
		StaffRefreshTokenResponse rotatedResponse = cacheManager.get(TokenConstant.ROTATED_KEY + oldHashedRefreshToken,
						StaffRefreshTokenResponse.class)
				.orElse(null);
		if(rotatedResponse != null){
			presenter.present(rotatedResponse);
			return;
		}
		UserSessionDomain currentSession = getCurrentSession(oldHashedRefreshToken);
		if(currentSession.sessionType() != SessionType.STAFF){
			throw new ExpiredSessionException("session expired");
		}
		Instant now = Instant.now();
		if(now.isAfter(currentSession.expirationTime())){
			throw new ExpiredSessionException("session expired");
		}
		String oldAccessToken = cacheManager.get(TokenConstant.ACCESS_KEY + oldHashedRefreshToken)
				.orElse(null);
		if(oldAccessToken != null){
			presenter.present(new StaffRefreshTokenResponse(oldAccessToken, null));
			return;
		}
		String newAccessToken = generateAccessToken(currentSession);
		long rotationDurationBeforeExpireInSeconds = tokenProcessor.getRotationDurationBeforeExpireInSeconds();
		Instant rotationTime = currentSession.expirationTime().minusSeconds(rotationDurationBeforeExpireInSeconds);
		if(now.isAfter(rotationTime)){
			UserSessionDomain gracedSession = graceCurrentSession(currentSession, now);
			String newRefreshToken = rotateToken(gracedSession, now, newAccessToken);
			StaffRefreshTokenResponse response = new StaffRefreshTokenResponse(newAccessToken, newRefreshToken);
			cacheManager.set(TokenConstant.ROTATED_KEY + oldHashedRefreshToken, response,
					Duration.ofSeconds(TokenConstant.ROTATION_GRACE_SECONDS));
			presenter.present(response);
			return;
		}
		updateAccessTokenCache(oldHashedRefreshToken, newAccessToken);
		presenter.present(new StaffRefreshTokenResponse(newAccessToken, null));
	}

	private UserSessionDomain getCurrentSession(String hashedRefreshToken){
		String cacheKey = TokenConstant.REFRESH_KEY + hashedRefreshToken;
		return cacheManager.get(cacheKey, UserSessionDomain.class)
				.orElseGet(() -> {
					UserSessionDomain session = gateway.findSessionById(hashedRefreshToken)
							.orElseThrow(() -> new ExpiredSessionException("session expired"));
					cacheSession(cacheKey, session);
					return session;
				});
	}

	private void cacheSession(String cacheKey, UserSessionDomain session){
		long remainingSeconds = Duration.between(Instant.now(), session.expirationTime()).getSeconds();
		if(remainingSeconds <= 0){
			return;
		}
		Duration duration = Duration.ofSeconds(Math.min(remainingSeconds, TokenConstant.REFRESH_CACHE_MAX_SECONDS));
		cacheManager.set(cacheKey, session, duration);
	}

	private String rotateToken(UserSessionDomain currentSession, Instant now, String newAccessToken){
		String newRefreshToken = tokenProcessor.generateRefreshToken();
		String newHashedRefreshToken = tokenProcessor.hashToken(newRefreshToken);
		Instant expirationTime = now.plusSeconds(tokenProcessor.getRefreshDurationInSeconds());
		UserSessionDomain newSession = gateway.save(UserSessionDomain.create(newHashedRefreshToken, expirationTime,
				currentSession.userId(), SessionType.STAFF));
		Duration duration = Duration.ofSeconds(Math.min(tokenProcessor.getRefreshDurationInSeconds(),
				TokenConstant.REFRESH_CACHE_MAX_SECONDS));
		cacheManager.set(TokenConstant.REFRESH_KEY + newSession.id(), newSession, duration);
		updateAccessTokenCache(newHashedRefreshToken, newAccessToken);
		return newRefreshToken;
	}

	private UserSessionDomain graceCurrentSession(UserSessionDomain session, Instant now){
		cacheManager.delete(TokenConstant.REFRESH_KEY + session.id());
		UserSessionDomain freshSession = gateway.findSessionById(session.id())
				.orElse(session);
		UserSessionDomain userSession = freshSession.toBuilder()
				.expirationTime(now.plusSeconds(TokenConstant.ROTATION_GRACE_SECONDS))
				.build();
		return gateway.save(userSession);
	}

	private void updateAccessTokenCache(String hashedRefreshToken, String accessToken){
		long cacheDurationInSeconds = tokenProcessor.getAccessDurationInSeconds()
				- TokenConstant.ACCESS_CACHE_EARLY_EXPIRY_SECONDS;
		if(cacheDurationInSeconds <= 0){
			return;
		}
		cacheManager.set(TokenConstant.ACCESS_KEY + hashedRefreshToken, accessToken,
				Duration.ofSeconds(cacheDurationInSeconds));
	}

	private String generateAccessToken(UserSessionDomain session){
		StaffLoginProjection staff = gateway.findById(session.userId())
				.orElseThrow(() -> new NotFoundException("userId not found"));
		TenantIdDomain tenantId = new TenantIdDomain(staff.tenantId());
		TenantLoginProjection tenant = gateway.findTenantById(tenantId)
				.orElseThrow(() -> new NotFoundException("tenant not found"));
		StaffRole role = StaffRole.findByValue(staff.roleId())
				.orElseThrow(() -> new NotFoundException("role not found"));
		UserPrincipal userToken = new UserPrincipal(staff.id(), staff.username(),
				staff.fullName(), tenant.fullName(), staff.tenantId(), SessionType.STAFF, role);
		return tokenProcessor.generateAccessToken(userToken);
	}

}
