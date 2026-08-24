package com.ferry.user.webservice.staff.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	@PostMapping("/staff/registration")
	public ResponseEntity<?> register(@RequestBody StaffRegistrationRequest request,
	                                  @AuthenticationPrincipal UserAuthPrincipal principal){
		StaffRegistrationWebPresenter presenter = new StaffRegistrationWebPresenter();
		staffRegistrationUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
