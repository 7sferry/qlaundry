package com.ferry.user.webservice.staff.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationUseCase;
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
public class StaffRegistrationWebController{
	private final StaffRegistrationUseCase staffRegistrationUseCase;

	@Transactional
	@PostMapping("/auth/staff/registration")
	public ResponseEntity<?> register(@RequestBody StaffRegistrationRequest request){
		StaffRegistrationWebPresenter presenter = new StaffRegistrationWebPresenter();
		staffRegistrationUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
