package com.ferry.user.webservice.staff.resetpassword;

import com.ferry.user.core.staff.resetpassword.StaffResetPasswordGateway;
import com.ferry.user.core.staff.resetpassword.StaffResetPasswordPresenter;
import com.ferry.user.core.staff.resetpassword.StaffResetPasswordResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class StaffResetPasswordWebPresenter implements StaffResetPasswordPresenter{
	private ResponseEntity<StaffResetPasswordWebResponse> responseEntity;

	@Override
	public void present(StaffResetPasswordResponse response){
		responseEntity = ResponseEntity.ok(new StaffResetPasswordWebResponse(response.message()));
	}

}
