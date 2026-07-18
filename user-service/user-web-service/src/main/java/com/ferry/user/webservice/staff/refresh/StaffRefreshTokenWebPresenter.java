package com.ferry.user.webservice.staff.refresh;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.login.StaffLoginPresenter;
import com.ferry.user.core.staff.login.StaffLoginResponse;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenPresenter;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenResponse;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.webservice.staff.login.StaffLoginWebResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@RequiredArgsConstructor
public class StaffRefreshTokenWebPresenter implements StaffRefreshTokenPresenter{

	private final HttpServletResponse servletResponse;
	private final TokenProcessor tokenProcessor;

	private ResponseEntity<StaffRefreshTokenWebResponse> responseEntity;

	@Override
	public void present(StaffRefreshTokenResponse response){
		responseEntity = ResponseEntity.ok(new StaffRefreshTokenWebResponse(response.accessToken(), response.refreshToken()));
//		servletResponse.addHeader(HttpHeaders.SET_COOKIE, getResponseCookie(response.refreshToken()).toString());
	}

	private ResponseCookie getResponseCookie(String refreshToken){
		return ResponseCookie.from(TokenConstant.REFRESH_TOKEN_COOKIE, refreshToken)
				.httpOnly(true)
				.secure(false)
				.path(TokenConstant.AUTH_PATH)
				.maxAge(tokenProcessor.getRefreshTokenExpirationInSeconds())
				.sameSite("Lax")
				.build();
	}

}
