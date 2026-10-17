package com.ferry.user.core.staff.login;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.staff.constant.LoginConstant;
import com.ferry.user.core.staff.constant.LoginLockKey;
import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.login.FailedToLoginException;
import com.ferry.user.domain.staff.login.LoginLockedException;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

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
	private final AuthAuditLog auditLog;

	@Override
	public void execute(StaffLoginRequest request, StaffLoginPresenter presenter){
		String subject = subjectOf(request);
		rejectIfLocked(subject);
		StaffLoginProjection staff = null;
		try{
			request.validate();
			staff = gateway.findByUsername(new Username(request.username()))
					.orElseThrow(() -> new InvalidPasswordException("userId not found"));
			if(!passwordTool.matches(request.password(), staff.password())){
				throw new InvalidPasswordException("password not match");
			}
			TenantLoginProjection tenant = gateway.findTenantById(new TenantId(staff.tenantId()))
					.orElseThrow(() -> new NotFoundException("tenant not found"));
			if(TenantStatus.findByValue(tenant.statusId()).orElse(null) != TenantStatus.ACTIVE){
				throw new FailedToLoginException("tenant not confirmed");
			}
			String refreshToken = tokenProcessor.generateRefreshToken();
			String hashedRefreshToken = tokenProcessor.hashToken(refreshToken);
			storeSession(hashedRefreshToken, staff);
			String accessToken = generateAccessToken(staff, tenant, hashedRefreshToken);
			cacheManager.delete(LoginLockKey.attempt(subject));
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.SUCCESS, staff.id(), subject);
			presenter.present(new StaffLoginResponse(accessToken, refreshToken));
		} catch (InvalidPasswordException | InvalidUsernameException e){
			registerFailedAttempt(subject);
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, userIdOf(staff), subject);
			throw new FailedToLoginException(e);
		} catch (FailedToLoginException e){
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, userIdOf(staff), subject);
			throw e;
		} catch (Exception e){
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, userIdOf(staff), subject);
			throw new FailedToLoginException(e);
		}
	}

	private String subjectOf(StaffLoginRequest request){
		return LoginLockKey.normalize(request == null ? null : request.username());
	}

	private String userIdOf(StaffLoginProjection staff){
		return staff == null ? null : staff.id();
	}

	private void rejectIfLocked(String subject){
		if(subject.isBlank()){
			return;
		}
		cacheManager.get(LoginLockKey.lock(subject)).ifPresent(unlockAt -> {
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.LOCKED, null, subject);
			throw new LoginLockedException(retryAfter(unlockAt));
		});
	}

	private Duration retryAfter(String unlockAt){
		try{
			Duration remaining = Duration.between(Instant.now(), Instant.parse(unlockAt));
			return remaining.isPositive() ? remaining : Duration.ofSeconds(1);
		} catch (DateTimeParseException e){
			return LoginConstant.LOCK_DURATION;
		}
	}

	private void registerFailedAttempt(String subject){
		if(subject.isBlank()){
			return;
		}
		long attempts = cacheManager.increment(LoginLockKey.attempt(subject), LoginConstant.ATTEMPT_WINDOW);
		if(attempts >= LoginConstant.MAX_FAILED_ATTEMPTS){
			cacheManager.set(LoginLockKey.lock(subject),
					Instant.now().plus(LoginConstant.LOCK_DURATION).toString(), LoginConstant.LOCK_DURATION);
			cacheManager.delete(LoginLockKey.attempt(subject));
		}
	}

	private String generateAccessToken(StaffLoginProjection staff, TenantLoginProjection tenant, String hashedRefreshToken){
		StaffRole role = StaffRole.findByValue(staff.roleId())
				.orElseThrow(() -> new NotFoundException("role not found"));
		UserAuthPrincipal userToken = new UserAuthPrincipal(staff.id(), staff.username(),
				staff.fullName(), tenant.fullName(), staff.tenantId(), tenant.timeZone(), SessionType.STAFF, role);
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
		UserSession userSession = gateway.save(UserSession.create(hashedRefreshToken, expirationTime,
				staff.id(), SessionType.STAFF));
		Duration duration = Duration.ofSeconds(Math.min(tokenProcessor.getRefreshDurationInSeconds(),
				TokenConstant.REFRESH_CACHE_MAX_SECONDS));
		cacheManager.set(TokenConstant.REFRESH_KEY + userSession.id(), userSession, duration);
	}

}
