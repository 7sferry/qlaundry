package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.HashedPasswordDomain;
import com.ferry.user.domain.RawPasswordDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.exception.InvalidOtpException;
import com.ferry.user.domain.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.StaffDomain;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
		UsernameDomain username = new UsernameDomain(request.username());
		RawPasswordDomain password = new RawPasswordDomain(request.password());
		validateResetToken(username, request.resetToken());
		StaffDomain staff = gateway.findByUsername(username)
				.orElseThrow(() -> new InvalidUsernameException("Invalid username"));
		HashedPasswordDomain hashedPassword = passwordTool.hash(password);
		StaffDomain updatedStaff = staff.toBuilder()
				.password(hashedPassword)
				.build();
		gateway.save(updatedStaff);
		presenter.present(new StaffResetPasswordResponse("password has been reset"));
	}

	private void validateResetToken(UsernameDomain username, String resetToken){
		String storedToken = cacheManager.getAndDelete(PasswordConstant.RESET_TOKEN_KEY + username.value())
				.orElseThrow(() -> new InvalidOtpException("Invalid reset token"));
		if(resetToken == null
				|| !MessageDigest.isEqual(storedToken.getBytes(StandardCharsets.UTF_8), resetToken.getBytes(StandardCharsets.UTF_8))){
			throw new InvalidOtpException("Invalid reset token");
		}
	}

}
