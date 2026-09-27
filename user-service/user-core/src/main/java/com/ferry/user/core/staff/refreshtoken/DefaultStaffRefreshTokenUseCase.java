package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.staff.refresh.ExpiredSessionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.domain.token.UserAuthPrincipal;
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
		if(request.refreshToken() == null || request.refreshToken().isBlank()){
			presenter.presentUnauthorized();
			return;
		}
		String oldRefreshToken = request.refreshToken();
		String oldHashedRefreshToken = tokenProcessor.hashToken(oldRefreshToken);
		StaffRefreshTokenResponse rotatedResponse = cacheManager.get(TokenConstant.ROTATED_KEY + oldHashedRefreshToken,
						StaffRefreshTokenResponse.class)
				.orElse(null);
		if(rotatedResponse != null){
			presenter.presentRotatedToken(rotatedResponse);
			return;
		}
		UserSession currentSession = getCurrentSession(oldHashedRefreshToken);
		if(currentSession.sessionType() != SessionType.STAFF){
			presenter.presentUnauthorized();
			return;
		}
		Instant now = Instant.now();
		if(now.isAfter(currentSession.expirationTime())){
			presenter.presentUnauthorized();
			return;
		}
		String oldAccessToken = cacheManager.get(TokenConstant.ACCESS_KEY + oldHashedRefreshToken)
				.orElse(null);
		if(oldAccessToken != null){
			presenter.presentCachedToken(new StaffRefreshTokenResponse(oldAccessToken, null));
			return;
		}
		String newAccessToken = generateAccessToken(currentSession);
		long rotationDurationBeforeExpireInSeconds = tokenProcessor.getRotationDurationBeforeExpireInSeconds();
		Instant rotationTime = currentSession.expirationTime().minusSeconds(rotationDurationBeforeExpireInSeconds);
		if(now.isAfter(rotationTime)){
			UserSession gracedSession = graceCurrentSession(currentSession, now);
			String newRefreshToken = rotateToken(gracedSession, now, newAccessToken);
			StaffRefreshTokenResponse response = new StaffRefreshTokenResponse(newAccessToken, newRefreshToken);
			cacheManager.set(TokenConstant.ROTATED_KEY + oldHashedRefreshToken, response,
					Duration.ofSeconds(TokenConstant.ROTATION_GRACE_SECONDS));
			presenter.presentRotatedToken(response);
			return;
		}
		updateAccessTokenCache(oldHashedRefreshToken, newAccessToken);
		presenter.presentCachedToken(new StaffRefreshTokenResponse(newAccessToken, null));
	}

	private UserSession getCurrentSession(String hashedRefreshToken){
		String cacheKey = TokenConstant.REFRESH_KEY + hashedRefreshToken;
		return cacheManager.get(cacheKey, UserSession.class)
				.orElseGet(() -> {
					UserSession session = gateway.findSessionById(hashedRefreshToken)
							.orElseThrow(() -> new ExpiredSessionException("session expired"));
					cacheSession(cacheKey, session);
					return session;
				});
	}

	private void cacheSession(String cacheKey, UserSession session){
		long remainingSeconds = Duration.between(Instant.now(), session.expirationTime()).getSeconds();
		if(remainingSeconds <= 0){
			return;
		}
		Duration duration = Duration.ofSeconds(Math.min(remainingSeconds, TokenConstant.REFRESH_CACHE_MAX_SECONDS));
		cacheManager.set(cacheKey, session, duration);
	}

	private String rotateToken(UserSession currentSession, Instant now, String newAccessToken){
		String newRefreshToken = tokenProcessor.generateRefreshToken();
		String newHashedRefreshToken = tokenProcessor.hashToken(newRefreshToken);
		Instant expirationTime = now.plusSeconds(tokenProcessor.getRefreshDurationInSeconds());
		UserSession newSession = gateway.save(UserSession.create(newHashedRefreshToken, expirationTime,
				currentSession.userId(), SessionType.STAFF));
		Duration duration = Duration.ofSeconds(Math.min(tokenProcessor.getRefreshDurationInSeconds(),
				TokenConstant.REFRESH_CACHE_MAX_SECONDS));
		cacheManager.set(TokenConstant.REFRESH_KEY + newSession.id(), newSession, duration);
		updateAccessTokenCache(newHashedRefreshToken, newAccessToken);
		return newRefreshToken;
	}

	private UserSession graceCurrentSession(UserSession session, Instant now){
		cacheManager.delete(TokenConstant.REFRESH_KEY + session.id());
		UserSession freshSession = gateway.findSessionById(session.id())
				.orElse(session);
		UserSession userSession = freshSession.toBuilder()
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

	private String generateAccessToken(UserSession session){
		StaffLoginProjection staff = gateway.findById(session.userId())
				.orElseThrow(() -> new NotFoundException("userId not found"));
		TenantId tenantId = new TenantId(staff.tenantId());
		TenantLoginProjection tenant = gateway.findTenantById(tenantId)
				.orElseThrow(() -> new NotFoundException("tenant not found"));
		StaffRole role = StaffRole.findByValue(staff.roleId())
				.orElseThrow(() -> new NotFoundException("role not found"));
		UserAuthPrincipal userToken = new UserAuthPrincipal(staff.id(), staff.username(),
				staff.fullName(), tenant.fullName(), staff.tenantId(), tenant.timeZone(), SessionType.STAFF, role);
		return tokenProcessor.generateAccessToken(userToken);
	}

}
