package com.ferry.user.core.staff.logout;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSession;
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
	private final AuthAuditLog auditLog;

	@Override
	public void execute(StaffLogoutRequest request, StaffLogoutPresenter presenter){
		try{
			request.validate();
		} catch (RuntimeException e){
			auditLog.record(AuthAction.STAFF_LOGOUT, AuthResult.FAILURE, null);
			throw e;
		}
		String refreshToken = request.refreshToken();
		String userId = refreshToken == null ? null : revokeSession(tokenProcessor.hashToken(refreshToken));
		auditLog.record(AuthAction.STAFF_LOGOUT, userId == null ? AuthResult.FAILURE : AuthResult.SUCCESS, userId);
		presenter.present(new StaffLogoutResponse("bye!"));
	}

	private String revokeSession(String hashedRefreshToken){
		UserSession currentSession = gateway.findSessionById(hashedRefreshToken)
				.orElse(null);
		if(currentSession == null || currentSession.sessionType() != SessionType.STAFF){
			return null;
		}
		Instant now = Instant.now();
		if(now.isBefore(currentSession.expirationTime())){
			expireSession(currentSession, now);
		}
		cacheManager.delete(TokenConstant.ROTATED_KEY + hashedRefreshToken);
		cacheManager.delete(TokenConstant.ACCESS_KEY + hashedRefreshToken);
		cacheManager.delete(TokenConstant.REFRESH_KEY + hashedRefreshToken);
		return currentSession.userId();
	}

	private void expireSession(UserSession currentSession, Instant now){
		UserSession userSession = currentSession.toBuilder()
				.expirationTime(now)
				.build();
		gateway.save(userSession);
	}

}
