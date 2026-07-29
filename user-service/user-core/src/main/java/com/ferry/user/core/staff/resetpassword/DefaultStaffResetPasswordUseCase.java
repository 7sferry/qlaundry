package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.RawPasswordDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.forgottenpassword.FailedToResetPasswordException;
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
		try{
			UsernameDomain username = new UsernameDomain(request.username());
			RawPasswordDomain password = new RawPasswordDomain(request.password());
			validateResetToken(username, request.resetToken());
			StaffDomain staff = gateway.findByUsername(username)
					.orElseThrow(() -> new FailedToResetPasswordException("Invalid username"));
			HashedPasswordDomain hashedPassword = passwordTool.hash(password);
			StaffDomain updatedStaff = staff.toBuilder()
					.password(hashedPassword)
					.build();
			gateway.save(updatedStaff);
			presenter.present(new StaffResetPasswordResponse("password has been reset"));
		} catch (FailedToResetPasswordException e){
			throw e;
		} catch (Exception e){
			throw new FailedToResetPasswordException(e);
		}
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
