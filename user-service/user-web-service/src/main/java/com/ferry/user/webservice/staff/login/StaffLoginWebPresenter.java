package com.ferry.user.webservice.staff.login;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.login.StaffLoginPresenter;
import com.ferry.user.core.staff.login.StaffLoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class StaffLoginWebPresenter implements StaffLoginPresenter{
	private static final String REFRESH_TOKEN = "refresh_token";
	public static final String AUTH_PATH = "/auth";

	private final HttpServletResponse servletResponse;

	private ResponseEntity<StaffLoginWebResponse> responseEntity;

	public StaffLoginWebPresenter(HttpServletResponse servletResponse){
		this.servletResponse = servletResponse;
	}

	@Override
	public void present(StaffLoginResponse response){
		responseEntity = ResponseEntity.ok(new StaffLoginWebResponse(response.accessToken(), response.refreshToken()));
		servletResponse.addHeader(HttpHeaders.SET_COOKIE, getResponseCookie(response.refreshToken()).toString());
	}

	private ResponseCookie getResponseCookie(String refreshToken){
		return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
				.httpOnly(true)
				.secure(false)
				.path(AUTH_PATH)
				.maxAge(TokenConstant.REFRESH_TOKEN_EXPIRATION_IN_SECONDS)
				.sameSite("Lax")
				.build();
	}

}
