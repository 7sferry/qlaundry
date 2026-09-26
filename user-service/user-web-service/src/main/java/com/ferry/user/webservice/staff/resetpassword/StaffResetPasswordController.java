package com.ferry.user.webservice.staff.resetpassword;

import com.ferry.user.core.staff.resetpassword.StaffResetPasswordRequest;
import com.ferry.user.core.staff.resetpassword.StaffResetPasswordUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
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
public class StaffResetPasswordController{
	private final StaffResetPasswordUseCase staffResetPasswordUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping("/auth/staff/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody StaffResetPasswordRequest request){
		WebStaffResetPasswordPresenter presenter = new WebStaffResetPasswordPresenter();
		staffResetPasswordUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
