package com.ferry.user.webservice.staff.submitotp;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordRequest;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpPresenter;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpRequest;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpUseCase;
import com.ferry.user.webservice.staff.forgottenpassword.WebStaffForgottenPasswordPresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffSubmitOtpController{
	private final StaffSubmitOtpUseCase staffSubmitOtpUseCase;

	@PostMapping("/auth/staff/submitOtp")
	public ResponseEntity<?> submitOtp(@RequestBody StaffSubmitOtpRequest request){
		WebStaffSubmitOtpPresenter presenter = new WebStaffSubmitOtpPresenter();
		staffSubmitOtpUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
