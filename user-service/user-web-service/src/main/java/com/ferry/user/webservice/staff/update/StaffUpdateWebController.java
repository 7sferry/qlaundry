package com.ferry.user.webservice.staff.update;

import com.ferry.user.core.staff.update.StaffUpdateRequest;
import com.ferry.user.core.staff.update.StaffUpdateUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffUpdateWebController{
	private final StaffUpdateUseCase staffUpdateUseCase;

	@Transactional
	@PutMapping("/staff/profile")
	public ResponseEntity<?> updateProfile(@RequestBody StaffUpdateRequest request,
	                                       @AuthenticationPrincipal UserAuthPrincipal principal){
		StaffUpdateWebPresenter presenter = new StaffUpdateWebPresenter();
		staffUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
