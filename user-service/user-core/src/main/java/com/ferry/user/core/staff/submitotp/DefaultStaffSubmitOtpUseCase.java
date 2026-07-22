package com.ferry.user.core.staff.submitotp;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.exception.InvalidOtpException;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.HexFormat;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffSubmitOtpUseCase implements StaffSubmitOtpUseCase {
	private static final int RESET_TOKEN_BYTES = 32;

	private final UserCacheManager cacheManager;

	@Override
	public void execute(StaffSubmitOtpRequest request, StaffSubmitOtpPresenter presenter){
		UsernameDomain username = new UsernameDomain(request.username());
		String otpKey = PasswordConstant.OTP_KEY + username.value();
		String otp = cacheManager.get(otpKey)
				.orElseThrow(() -> new InvalidOtpException("Invalid otp"));
		if(!otp.equals(request.otp())){
			throw new InvalidOtpException("Invalid otp");
		}
		cacheManager.delete(otpKey);
		String resetToken = generateResetToken();
		cacheManager.set(PasswordConstant.RESET_TOKEN_KEY + username.value(), resetToken,
				PasswordConstant.RESET_TOKEN_DURATION_MINUTES);
		presenter.present(new StaffSubmitOtpResponse(resetToken));
	}

	private String generateResetToken(){
		byte[] tokenBytes = new byte[RESET_TOKEN_BYTES];
		PasswordConstant.GENERATOR.nextBytes(tokenBytes);
		return HexFormat.of().formatHex(tokenBytes);
	}

}
