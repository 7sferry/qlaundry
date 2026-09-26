package com.ferry.user.webservice.staff.submitotp;

import com.ferry.user.core.staff.submitotp.StaffSubmitOtpPresenter;
import com.ferry.user.core.staff.submitotp.StaffSubmitOtpResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class WebStaffSubmitOtpPresenter implements StaffSubmitOtpPresenter{
	private ResponseEntity<StaffSubmitOtpRestResponse> responseEntity;

	@Override
	public void present(StaffSubmitOtpResponse response){
		responseEntity = ResponseEntity.ok(new StaffSubmitOtpRestResponse(response.resetToken()));
	}

}
