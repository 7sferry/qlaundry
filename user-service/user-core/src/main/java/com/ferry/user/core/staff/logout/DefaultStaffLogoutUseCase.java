package com.ferry.user.core.staff.logout;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffLogoutUseCase implements StaffLogoutUseCase{
	private final StaffLogoutGateway gateway;
	private final TokenProcessor tokenProcessor;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(StaffLogoutRequest request, StaffLogoutPresenter presenter){
		request.validate();
		String refreshToken = request.refreshToken();
		if(refreshToken != null){
			revokeSession(tokenProcessor.hashToken(refreshToken));
		}
		presenter.present(new StaffLogoutResponse("bye!"));
	}

	private void revokeSession(String hashedRefreshToken){
		UserSessionDomain currentSession = gateway.findSessionById(hashedRefreshToken)
				.orElse(null);
		if(currentSession == null || currentSession.sessionType() != SessionType.STAFF){
			return;
		}
		Instant now = Instant.now();
		if(now.isBefore(currentSession.expirationTime())){
			expireSession(currentSession, now);
		}
		cacheManager.delete(TokenConstant.ROTATED_KEY + hashedRefreshToken);
		cacheManager.delete(TokenConstant.ACCESS_KEY + hashedRefreshToken);
		cacheManager.delete(TokenConstant.REFRESH_KEY + hashedRefreshToken);
	}

	private void expireSession(UserSessionDomain currentSession, Instant now){
		UserSessionDomain userSession = currentSession.toBuilder()
				.expirationTime(now)
				.build();
		gateway.save(userSession);
	}

}
