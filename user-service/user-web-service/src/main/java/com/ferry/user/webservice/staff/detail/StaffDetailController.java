package com.ferry.user.webservice.staff.detail;

import com.ferry.user.core.staff.detail.StaffDetailRequest;
import com.ferry.user.core.staff.detail.StaffDetailUseCase;
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
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffDetailController{
	private final StaffDetailUseCase  staffDetailUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/staff/detail")
	public ResponseEntity<?> getDetail(StaffDetailRequest request, @AuthenticationPrincipal UserAuthPrincipal principal){
		WebStaffDetailPresenter presenter = new WebStaffDetailPresenter();
		staffDetailUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
