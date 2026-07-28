package com.ferry.user.webservice.staff.delete;

import com.ferry.user.core.staff.delete.StaffDeleteRequest;
import com.ferry.user.core.staff.delete.StaffDeleteUseCase;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffDeleteWebController{
	private final StaffDeleteUseCase staffDeleteUseCase;

	@Transactional
	@DeleteMapping("/staff/delete")
	public ResponseEntity<?> delete(StaffDeleteRequest request, @AuthenticationPrincipal UserPrincipal principal){
		StaffDeleteWebPresenter presenter = new StaffDeleteWebPresenter();
		staffDeleteUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
