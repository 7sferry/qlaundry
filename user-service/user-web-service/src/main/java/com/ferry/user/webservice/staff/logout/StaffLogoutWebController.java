package com.ferry.user.webservice.staff.logout;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.logout.StaffLogoutRequest;
import com.ferry.user.core.staff.logout.StaffLogoutUseCase;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
@RestController
public class StaffLogoutWebController{
	private final StaffLogoutUseCase staffLogoutUseCase;

	@DeleteMapping("/auth/staff/logout")
	public ResponseEntity<?> logout(@CookieValue(value = TokenConstant.REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
	                                HttpServletResponse response){
		StaffLogoutWebPresenter presenter = new StaffLogoutWebPresenter(response);
		staffLogoutUseCase.execute(new StaffLogoutRequest(refreshToken), presenter);
		return presenter.getResponseEntity();
	}
}
