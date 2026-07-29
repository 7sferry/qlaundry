package com.ferry.user.core.staff.submitotp;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.submitotp.FailedToSubmitOtpException;
import lombok.RequiredArgsConstructor;

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
		try{
			request.validate();
			UsernameDomain username = new UsernameDomain(request.username());
			String otpKey = PasswordConstant.OTP_KEY + username.value();
			String otp = cacheManager.get(otpKey)
					.orElseThrow(() -> new FailedToSubmitOtpException("Invalid otp"));
			if(!otp.equals(request.otp())){
				throw new FailedToSubmitOtpException("Invalid otp");
			}
			cacheManager.delete(otpKey);
			String resetToken = generateResetToken();
			cacheManager.set(PasswordConstant.RESET_TOKEN_KEY + username.value(), resetToken,
					PasswordConstant.RESET_TOKEN_DURATION);
			presenter.present(new StaffSubmitOtpResponse(resetToken));
		} catch (FailedToSubmitOtpException e){
			throw e;
		} catch (Exception e){
			throw new FailedToSubmitOtpException(e);
		}
	}

	private String generateResetToken(){
		byte[] tokenBytes = new byte[RESET_TOKEN_BYTES];
		PasswordConstant.getRandom().nextBytes(tokenBytes);
		return HexFormat.of().formatHex(tokenBytes);
	}

}
