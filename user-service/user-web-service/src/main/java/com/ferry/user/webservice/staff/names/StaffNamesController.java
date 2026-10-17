package com.ferry.user.webservice.staff.names;

import com.ferry.user.core.staff.names.StaffNamesRequest;
import com.ferry.user.core.staff.names.StaffNamesUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffNamesController{
	private final StaffNamesUseCase staffNamesUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/staff/names")
	public ResponseEntity<?> getNames(StaffNamesRequest request, @AuthenticationPrincipal UserAuthPrincipal principal){
		WebStaffNamesPresenter presenter = new WebStaffNamesPresenter();
		staffNamesUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
