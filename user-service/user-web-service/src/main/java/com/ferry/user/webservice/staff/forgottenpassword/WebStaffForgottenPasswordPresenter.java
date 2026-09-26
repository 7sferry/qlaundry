package com.ferry.user.webservice.staff.forgottenpassword;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordPresenter;
import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class WebStaffForgottenPasswordPresenter implements StaffForgottenPasswordPresenter{
	private ResponseEntity<StaffForgottenPasswordRestResponse> responseEntity;
	@Override
	public void present(StaffForgottenPasswordResponse response){
		responseEntity = ResponseEntity.ok(new StaffForgottenPasswordRestResponse(response.email()));
	}
}
