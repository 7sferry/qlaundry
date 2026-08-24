package com.ferry.user.webservice.staff.refresh;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenPresenter;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenResponse;
import com.ferry.user.core.tools.TokenProcessor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;

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
	public void presentRotatedToken(StaffRefreshTokenResponse response){
		responseEntity = ResponseEntity.ok(new StaffRefreshTokenWebResponse(response.accessToken(), response.refreshToken()));
		servletResponse.addHeader(HttpHeaders.SET_COOKIE, getResponseCookie(response.refreshToken()).toString());
	}

	@Override
	public void presentCachedToken(StaffRefreshTokenResponse response){
		responseEntity = ResponseEntity.ok(new StaffRefreshTokenWebResponse(response.accessToken(), response.refreshToken()));
	}

	@Override
	public void presentUnauthorized(){
		responseEntity = ResponseEntity.of(ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)).build();
		servletResponse.addHeader(HttpHeaders.SET_COOKIE, getResponseCookie("").toString());
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
