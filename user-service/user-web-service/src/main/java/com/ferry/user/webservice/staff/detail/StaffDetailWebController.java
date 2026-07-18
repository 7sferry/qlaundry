package com.ferry.user.webservice.staff.detail;

import com.ferry.user.core.staff.detail.StaffDetailRequest;
import com.ferry.user.core.staff.detail.StaffDetailUseCase;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffDetailWebController{
	private final StaffDetailUseCase  staffDetailUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/staff/detail")
	public ResponseEntity<?> getDetail(StaffDetailRequest request, @AuthenticationPrincipal UserPrincipal principal){
		StaffDetailWebPresenter presenter = new StaffDetailWebPresenter();
		staffDetailUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
