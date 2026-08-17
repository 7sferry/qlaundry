package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.RawPasswordDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.forgottenpassword.FailedToResetPasswordException;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffResetPasswordUseCase implements StaffResetPasswordUseCase{
	private final StaffResetPasswordGateway gateway;
	private final PasswordTool passwordTool;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(StaffResetPasswordRequest request, StaffResetPasswordPresenter presenter){
		try{
			request.validate();
			UsernameDomain username = new UsernameDomain(request.username());
			RawPasswordDomain password = new RawPasswordDomain(request.password());
			validateResetToken(username, request.resetToken());
			StaffDomain staff = gateway.findByUsername(username)
					.orElseThrow(() -> new FailedToResetPasswordException("Invalid username"));
			validateRecentPassword(staff, password);
			validateLastUsedPasswords(staff, password);
			HashedPasswordDomain hashedPassword = passwordTool.hash(password);
			gateway.save(StaffPasswordDomain.register(staff.id(), hashedPassword, staff.id()));
			presenter.present(new StaffResetPasswordResponse("password has been reset"));
		} catch (FailedToResetPasswordException e){
			throw e;
		} catch (Exception e){
			throw new FailedToResetPasswordException(e);
		}
	}

	private void validateLastUsedPasswords(StaffDomain staff, RawPasswordDomain password){
		List<StaffPasswordProjection> recentPasswords = gateway.findRecentPasswords(staff.id(),
				Instant.now().minus(PasswordConstant.PASSWORD_REUSE_WINDOW));
		boolean reused = recentPasswords.stream()
				.anyMatch(recent -> passwordTool.matches(password.value(), recent.password()));
		if(reused){
			throw new InvalidPasswordException("Password was used within the last 3 months, please choose a different password");
		}
	}

	private void validateRecentPassword(StaffDomain staff, RawPasswordDomain password){
		gateway.findCurrentPassword(staff.id())
				.ifPresent(current -> {
					if(passwordTool.matches(password.value(), current.password())){
						throw new InvalidPasswordException("New password must be different from your current password");
					}
				});
	}

	private void validateResetToken(UsernameDomain username, String resetToken){
		String storedToken = cacheManager.getAndDelete(PasswordConstant.RESET_TOKEN_KEY + username.value())
				.orElseThrow(() -> new FailedToResetPasswordException("Invalid reset token"));
		if(resetToken == null
				|| !MessageDigest.isEqual(storedToken.getBytes(StandardCharsets.UTF_8), resetToken.getBytes(StandardCharsets.UTF_8))){
			throw new FailedToResetPasswordException("Invalid reset token");
		}
	}

}
