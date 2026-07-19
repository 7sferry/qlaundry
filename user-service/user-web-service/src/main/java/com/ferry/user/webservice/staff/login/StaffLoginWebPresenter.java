package com.ferry.user.webservice.staff.login;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.login.StaffLoginPresenter;
import com.ferry.user.core.staff.login.StaffLoginResponse;
import com.ferry.user.core.tools.TokenProcessor;
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
public class StaffLoginWebPresenter implements StaffLoginPresenter{

	private final HttpServletResponse servletResponse;
	private final TokenProcessor tokenProcessor;

	private ResponseEntity<StaffLoginWebResponse> responseEntity;

	@Override
	public void present(StaffLoginResponse response){
		responseEntity = ResponseEntity.ok(new StaffLoginWebResponse(response.accessToken(), response.refreshToken()));
		servletResponse.addHeader(HttpHeaders.SET_COOKIE, getResponseCookie(response.refreshToken()).toString());
	}

	private ResponseCookie getResponseCookie(String refreshToken){
		return ResponseCookie.from(TokenConstant.REFRESH_TOKEN_COOKIE, refreshToken)
				.httpOnly(true)
				.secure(false)
				.path(TokenConstant.AUTH_PATH)
				.maxAge(tokenProcessor.getRefreshDurationInSeconds())
				.sameSite("Lax")
				.build();
	}

}
