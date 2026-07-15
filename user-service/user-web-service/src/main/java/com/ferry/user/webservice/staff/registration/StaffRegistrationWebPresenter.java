package com.ferry.user.webservice.staff.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationPresenter;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class StaffRegistrationWebPresenter implements StaffRegistrationPresenter{
	private ResponseEntity<StaffRegistrationWebResponse> responseEntity;

	@Override
	public void present(StaffRegistrationResponse response){
		responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(new StaffRegistrationWebResponse(response.user().usernameValue()));
	}
}
