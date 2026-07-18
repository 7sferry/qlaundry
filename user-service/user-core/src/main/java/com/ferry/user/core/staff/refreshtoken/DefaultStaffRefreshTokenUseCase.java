package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.exception.ExpiredSessionException;
import com.ferry.user.domain.exception.NotFoundException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.staff.refresh.StaffRefreshTokenProjection;
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

	@Override
	public void execute(StaffRefreshTokenRequest request, StaffRefreshTokenPresenter presenter){
		String hashedRefreshToken = tokenProcessor.hashToken(request.refreshToken());
		StaffRefreshTokenProjection session = gateway.findSessionById(hashedRefreshToken)
				.orElseThrow(() -> new NotFoundException("Refresh token not found"));
		Instant expirationTime = session.expirationTime();
		Instant now = Instant.now();
		if(expirationTime.isBefore(now)){
			throw new ExpiredSessionException("Refresh token is expired");
		}
		StaffLoginProjection staff = gateway.findByUsername(new UsernameDomain(session.userId()))
				.orElseThrow(() -> new NotFoundException("username not found"));
		String accessToken = generateAccessToken(staff, hashedRefreshToken);
		presenter.present(new StaffRefreshTokenResponse(accessToken, request.refreshToken()));
	}

	private String generateAccessToken(StaffLoginProjection staff, String hashedRefreshToken){
		TenantIdDomain tenantId = new TenantIdDomain(staff.tenantId());
		TenantLoginProjection tenant = gateway.findTenantById(tenantId)
				.orElseThrow(() -> new NotFoundException("tenant not found"));
		UserPrincipal userToken = new UserPrincipal(staff.username(),
				staff.fullName(), tenant.fullName(), staff.tenantId(), SessionType.STAFF);
		String accessToken = tokenProcessor.generateAccessToken(userToken);
		Duration expirationTime = Duration.ofSeconds(tokenProcessor.getAccessTokenExpirationInSeconds());
//		gateway.cache(TokenConstant.ACCESS_KEY + hashedRefreshToken, accessToken, expirationTime);
		return accessToken;
	}

}
