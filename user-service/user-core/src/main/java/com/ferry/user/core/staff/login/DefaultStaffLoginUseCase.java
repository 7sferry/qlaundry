package com.ferry.user.core.staff.login;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.login.FailedToLoginException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
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
public class DefaultStaffLoginUseCase implements StaffLoginUseCase {
	private final StaffLoginGateway gateway;
	private final PasswordTool passwordTool;
	private final TokenProcessor tokenProcessor;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(StaffLoginRequest request, StaffLoginPresenter presenter){
		try{
			request.validate();
			StaffLoginProjection staff = gateway.findByUsername(new UsernameDomain(request.username()))
					.orElseThrow(() -> new InvalidPasswordException("userId not found"));
			if(!passwordTool.matches(request.password(), staff.password())){
				throw new InvalidPasswordException("password not match");
			}
			TenantLoginProjection tenant = gateway.findTenantById(new TenantIdDomain(staff.tenantId()))
					.orElseThrow(() -> new NotFoundException("tenant not found"));
			if(TenantStatus.findByValue(tenant.statusId()).orElse(null) != TenantStatus.ACTIVE){
				throw new FailedToLoginException("tenant not confirmed");
			}
			String refreshToken = tokenProcessor.generateRefreshToken();
			String hashedRefreshToken = tokenProcessor.hashToken(refreshToken);
			storeSession(hashedRefreshToken, staff);
			String accessToken = generateAccessToken(staff, tenant, hashedRefreshToken);
			presenter.present(new StaffLoginResponse(accessToken, refreshToken));
		} catch (FailedToLoginException e){
			throw e;
		} catch (Exception e){
			throw new FailedToLoginException(e);
		}
	}

	private String generateAccessToken(StaffLoginProjection staff, TenantLoginProjection tenant, String hashedRefreshToken){
		StaffRole role = StaffRole.findByValue(staff.roleId())
				.orElseThrow(() -> new NotFoundException("role not found"));
		UserPrincipal userToken = new UserPrincipal(staff.id(), staff.username(),
				staff.fullName(), tenant.fullName(), staff.tenantId(), SessionType.STAFF, role);
		String accessToken = tokenProcessor.generateAccessToken(userToken);
		long cacheDurationInSeconds = tokenProcessor.getAccessDurationInSeconds()
				- TokenConstant.ACCESS_CACHE_EARLY_EXPIRY_SECONDS;
		if(cacheDurationInSeconds > 0){
			cacheManager.set(TokenConstant.ACCESS_KEY + hashedRefreshToken, accessToken,
					Duration.ofSeconds(cacheDurationInSeconds));
		}
		return accessToken;
	}

	private void storeSession(String hashedRefreshToken, StaffLoginProjection staff){
		Instant expirationTime = Instant.now().plusSeconds(tokenProcessor.getRefreshDurationInSeconds());
		UserSessionDomain userSession = gateway.save(UserSessionDomain.create(hashedRefreshToken, expirationTime,
				staff.id(), SessionType.STAFF));
		Duration duration = Duration.ofSeconds(Math.min(tokenProcessor.getRefreshDurationInSeconds(),
				TokenConstant.REFRESH_CACHE_MAX_SECONDS));
		cacheManager.set(TokenConstant.REFRESH_KEY + userSession.id(), userSession, duration);
	}

}
