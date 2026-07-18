package com.ferry.user.core.staff.login;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenGenerator;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.exception.InvalidPasswordException;
import com.ferry.user.domain.exception.NotFoundException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.token.UserTokenDomain;
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
	private final TokenGenerator tokenGenerator;

	@Override
	public void execute(StaffLoginRequest request, StaffLoginPresenter presenter){
		StaffLoginProjection staff = gateway.findByUsername(request.username())
				.orElseThrow(() -> new NotFoundException("username not found"));
		if(!passwordTool.matches(request.password(), staff.password())){
			throw new InvalidPasswordException("password not match");
		}
		String refreshToken = tokenGenerator.generateRefreshToken();
		String hashedRefreshToken = tokenGenerator.hashToken(refreshToken);
		storeSession(hashedRefreshToken, staff);
		String accessToken = generateAccessToken(staff, hashedRefreshToken);
		presenter.present(new StaffLoginResponse(accessToken, refreshToken));
	}

	private String generateAccessToken(StaffLoginProjection staff, String hashedRefreshToken){
		TenantDomain tenant = gateway.findTenantById(staff.tenantId())
				.orElseThrow(() -> new NotFoundException("tenant not found"));
		UserTokenDomain userToken = new UserTokenDomain(new UsernameDomain(staff.username()),
				new FullNameDomain(staff.fullName()), new FullNameDomain(tenant.fullNameValue()), SessionType.STAFF);
		String accessToken = tokenGenerator.generateAccessToken(userToken);
		Duration expirationTime = Duration.ofSeconds(TokenConstant.ACCESS_TOKEN_EXPIRATION_IN_SECONDS);
		gateway.cache(TokenConstant.ACCESS_KEY + hashedRefreshToken, accessToken, expirationTime);
		return accessToken;
	}

	private void storeSession(String hashedRefreshToken, StaffLoginProjection staff){
		Instant expirationTime = Instant.now().plusSeconds(TokenConstant.REFRESH_TOKEN_EXPIRATION_IN_SECONDS);
		UserSessionDomain userSession = gateway.save(UserSessionDomain.create(hashedRefreshToken, expirationTime,
				staff.id(), SessionType.STAFF));
		gateway.cache(TokenConstant.REFRESH_KEY, userSession, Duration.ofHours(1));
	}

}
