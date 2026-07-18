package com.ferry.user.webservice.staff.list;

import com.ferry.user.core.staff.list.StaffListRequest;
import com.ferry.user.core.staff.list.StaffListUseCase;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffListWebController{
	private final StaffListUseCase staffListUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/staff/list")
	public ResponseEntity<?> getList(StaffListRequest request, @AuthenticationPrincipal UserPrincipal principal){
		StaffListWebPresenter presenter = new StaffListWebPresenter();
		staffListUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
