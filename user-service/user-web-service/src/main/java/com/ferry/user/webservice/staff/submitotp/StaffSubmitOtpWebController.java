package com.ferry.user.webservice.staff.submitotp;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordRequest;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpPresenter;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpRequest;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpUseCase;
import com.ferry.user.webservice.staff.forgottenpassword.StaffForgottenPasswordWebPresenter;
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
public class StaffSubmitOtpWebController{
	private final StaffSubmitOtpUseCase staffSubmitOtpUseCase;

	@Transactional
	@PostMapping("/auth/staff/submitOtp")
	public ResponseEntity<?> submitOtp(@RequestBody StaffSubmitOtpRequest request){
		StaffSubmitOtpWebPresenter presenter = new StaffSubmitOtpWebPresenter();
		staffSubmitOtpUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
