package com.ferry.user.core.staff.login;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.exception.InvalidPasswordException;
import com.ferry.user.domain.exception.NotFoundException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
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
public class DefaultStaffLoginUseCase implements StaffLoginUseCase {
	private final StaffLoginGateway gateway;
	private final PasswordTool passwordTool;
	private final TokenProcessor tokenProcessor;

	@Override
	public void execute(StaffLoginRequest request, StaffLoginPresenter presenter){
		StaffLoginProjection staff = gateway.findByUsername(new UsernameDomain(request.username()))
				.orElseThrow(() -> new NotFoundException("username not found"));
		if(!passwordTool.matches(request.password(), staff.password())){
			throw new InvalidPasswordException("password not match");
		}
		String refreshToken = tokenProcessor.generateRefreshToken();
		String hashedRefreshToken = tokenProcessor.hashToken(refreshToken);
		storeSession(hashedRefreshToken, staff);
		String accessToken = generateAccessToken(staff, hashedRefreshToken);
		presenter.present(new StaffLoginResponse(accessToken, refreshToken));
	}

	private String generateAccessToken(StaffLoginProjection staff, String hashedRefreshToken){
		TenantIdDomain tenantId = new TenantIdDomain(staff.tenantId());
		TenantLoginProjection tenant = gateway.findTenantById(tenantId)
				.orElseThrow(() -> new NotFoundException("tenant not found"));
		UserPrincipal userToken = new UserPrincipal(staff.username(),
				staff.fullName(), tenant.fullName(), staff.tenantId(), SessionType.STAFF);
		String accessToken = tokenProcessor.generateAccessToken(userToken);
		Duration expirationTime = Duration.ofSeconds(tokenProcessor.getAccessTokenExpirationInSeconds());
		gateway.cache(TokenConstant.ACCESS_KEY + hashedRefreshToken, accessToken, expirationTime);
		return accessToken;
	}

	private void storeSession(String hashedRefreshToken, StaffLoginProjection staff){
		Instant expirationTime = Instant.now().plusSeconds(tokenProcessor.getRefreshTokenExpirationInSeconds());
		UserSessionDomain userSession = gateway.save(UserSessionDomain.create(hashedRefreshToken, expirationTime,
				staff.username(), SessionType.STAFF));
		gateway.cache(TokenConstant.REFRESH_KEY, userSession, Duration.ofHours(1));
	}

}
