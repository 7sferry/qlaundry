package com.ferry.user.webservice.staff.logout;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.logout.StaffLogoutPresenter;
import com.ferry.user.core.staff.logout.StaffLogoutResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
@Getter
public class StaffLogoutWebPresenter implements StaffLogoutPresenter{

	private final HttpServletResponse servletResponse;

	private ResponseEntity<StaffLogoutWebResponse> responseEntity;

	@Override
	public void present(StaffLogoutResponse staffLogoutResponse){
		responseEntity = ResponseEntity.ok(new StaffLogoutWebResponse(staffLogoutResponse.message()));
		servletResponse.addHeader(HttpHeaders.SET_COOKIE, getResponseCookie().toString());
	}

	private ResponseCookie getResponseCookie(){
		return ResponseCookie.from(TokenConstant.REFRESH_TOKEN_COOKIE, null)
				.httpOnly(true)
				.secure(false)
				.path(TokenConstant.AUTH_PATH)
				.maxAge(0)
				.sameSite("Lax")
				.build();
	}

}
