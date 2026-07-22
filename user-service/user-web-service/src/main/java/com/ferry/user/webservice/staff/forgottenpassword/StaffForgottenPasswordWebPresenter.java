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
public class StaffForgottenPasswordWebPresenter implements StaffForgottenPasswordPresenter{
	private ResponseEntity<StaffForgottenPasswordWebResponse> responseEntity;
	@Override
	public void present(StaffForgottenPasswordResponse response){
		responseEntity = ResponseEntity.ok(new StaffForgottenPasswordWebResponse(response.email()));
	}
}
