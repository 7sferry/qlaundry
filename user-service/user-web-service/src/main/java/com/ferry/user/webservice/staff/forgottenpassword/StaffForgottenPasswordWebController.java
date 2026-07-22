package com.ferry.user.webservice.staff.forgottenpassword;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordRequest;
import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordUseCase;
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
public class StaffForgottenPasswordWebController{

	private final StaffForgottenPasswordUseCase staffForgottenPasswordUseCase;

	@Transactional
	@PostMapping("/auth/staff/forgottenPassword")
	public ResponseEntity<?> forgottenPassword(@RequestBody StaffForgottenPasswordRequest request){
		StaffForgottenPasswordWebPresenter presenter = new StaffForgottenPasswordWebPresenter();
		staffForgottenPasswordUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
