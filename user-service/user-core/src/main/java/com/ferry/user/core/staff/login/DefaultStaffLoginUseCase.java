package com.ferry.user.core.staff.login;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.staff.constant.LoginAttemptKey;
import com.ferry.user.core.staff.constant.LoginConstant;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tenant.registration.VerificationGateway;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.login.FailedToLoginException;
import com.ferry.user.domain.staff.login.LoginChallenge;
import com.ferry.user.domain.staff.login.LoginChallengeRequiredException;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
	private final VerificationGateway verificationGateway;
	private final AuthAuditLog auditLog;

	@Override
	public void execute(StaffLoginRequest request, StaffLoginPresenter presenter){
		String subject = subjectOf(request);
		long attempts = failedAttempts(subject);
		StaffLoginProjection staff = null;
		try{
			request.validate();
			LoginChallenge challenge = challengeFor(attempts);
			if(challenge != LoginChallenge.OTP && isBlank(request.password())){
				throw new FailedToLoginException("password required", challenge);
			}
			requireChallengeProof(request, challenge);
			Username username = new Username(request.username());
			staff = challenge == LoginChallenge.OTP
					? authenticateWithOtp(request, username, subject)
					: authenticateWithPassword(request, username);
			TenantLoginProjection tenant = gateway.findTenantById(new TenantId(staff.tenantId()))
					.orElseThrow(() -> new NotFoundException("tenant not found"));
			if(TenantStatus.findByValue(tenant.statusId()).orElse(null) != TenantStatus.ACTIVE){
				throw new FailedToLoginException("tenant not confirmed", challenge);
			}
			String refreshToken = tokenProcessor.generateRefreshToken();
			String hashedRefreshToken = tokenProcessor.hashToken(refreshToken);
			storeSession(hashedRefreshToken, staff);
			String accessToken = generateAccessToken(staff, tenant, hashedRefreshToken);
			cacheManager.delete(LoginAttemptKey.attempt(subject));
			cacheManager.delete(LoginAttemptKey.otpAttempt(subject));
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.SUCCESS, staff.id(), subject);
			presenter.present(new StaffLoginResponse(accessToken, refreshToken));
		} catch (LoginChallengeRequiredException e){
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.CHALLENGED, null, subject);
			throw e;
		} catch (InvalidPasswordException | InvalidUsernameException e){
			long updatedAttempts = registerFailedAttempt(subject);
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, userIdOf(staff), subject);
			throw new FailedToLoginException(e, challengeFor(updatedAttempts));
		} catch (FailedToLoginException e){
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, userIdOf(staff), subject);
			throw e;
		} catch (Exception e){
			auditLog.record(AuthAction.STAFF_LOGIN, AuthResult.FAILURE, userIdOf(staff), subject);
			throw new FailedToLoginException(e, challengeFor(attempts));
		}
	}

	private String subjectOf(StaffLoginRequest request){
		return LoginAttemptKey.normalize(request == null ? null : request.username());
	}

	private String userIdOf(StaffLoginProjection staff){
		return staff == null ? null : staff.id();
	}

	private LoginChallenge challengeFor(long attempts){
		if(attempts >= LoginConstant.OTP_AFTER_ATTEMPTS){
			return LoginChallenge.OTP;
		}
		if(attempts >= LoginConstant.CAPTCHA_AFTER_ATTEMPTS){
			return LoginChallenge.CAPTCHA;
		}
		return null;
	}

	private long failedAttempts(String subject){
		if(subject.isBlank()){
			return 0;
		}
		return cacheManager.get(LoginAttemptKey.attempt(subject)).map(this::parseCount).orElse(0L);
	}

	private long parseCount(String value){
		try{
			return Long.parseLong(value);
		} catch (NumberFormatException e){
			return 0;
		}
	}

	private void requireChallengeProof(StaffLoginRequest request, LoginChallenge challenge){
		if(challenge == LoginChallenge.OTP && isBlank(request.otp())){
			throw new LoginChallengeRequiredException(LoginChallenge.OTP);
		}
		if(challenge == LoginChallenge.CAPTCHA
				&& (isBlank(request.captchaToken()) || !verificationGateway.verify(request.captchaToken()))){
			throw new LoginChallengeRequiredException(LoginChallenge.CAPTCHA);
		}
	}

	private boolean isBlank(String value){
		return value == null || value.isBlank();
	}

	private StaffLoginProjection authenticateWithPassword(StaffLoginRequest request, Username username){
		StaffLoginProjection staff = gateway.findByUsername(username)
				.orElseThrow(() -> new InvalidPasswordException("userId not found"));
		if(!passwordTool.matches(request.password(), staff.password())){
			throw new InvalidPasswordException("password not match");
		}
		return staff;
	}

	private StaffLoginProjection authenticateWithOtp(StaffLoginRequest request, Username username, String subject){
		String otpKey = PasswordConstant.OTP_KEY + username.value();
		boolean valid = cacheManager.get(otpKey)
				.map(expected -> MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
						request.otp().getBytes(StandardCharsets.UTF_8)))
				.orElse(false);
		if(!valid){
			registerFailedOtp(subject, otpKey);
			throw new InvalidPasswordException("otp not match");
		}
		cacheManager.delete(otpKey);
		return gateway.findByUsername(username)
				.orElseThrow(() -> new InvalidPasswordException("userId not found"));
	}

	private void registerFailedOtp(String subject, String otpKey){
		long failures = cacheManager.increment(LoginAttemptKey.otpAttempt(subject), PasswordConstant.OTP_DURATION);
		if(failures >= LoginConstant.OTP_MAX_FAILED_ATTEMPTS){
			cacheManager.delete(otpKey);
			cacheManager.delete(LoginAttemptKey.otpAttempt(subject));
		}
	}

	private long registerFailedAttempt(String subject){
		if(subject.isBlank()){
			return 0;
		}
		return cacheManager.increment(LoginAttemptKey.attempt(subject), LoginConstant.ATTEMPT_WINDOW);
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
