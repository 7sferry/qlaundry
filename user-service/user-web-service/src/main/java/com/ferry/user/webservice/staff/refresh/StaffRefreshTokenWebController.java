package com.ferry.user.webservice.staff.refresh;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenRequest;
import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenUseCase;
import com.ferry.user.core.tools.TokenProcessor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffRefreshTokenWebController{
	private final StaffRefreshTokenUseCase staffRefreshTokenUseCase;
	private final TokenProcessor tokenProcessor;

	@Transactional
	@PostMapping("/auth/staff/refresh")
	public ResponseEntity<?> staffRefreshToken(@CookieValue(value = TokenConstant.REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
	                                           HttpServletResponse response){
		StaffRefreshTokenWebPresenter presenter = new StaffRefreshTokenWebPresenter(response, tokenProcessor);
		staffRefreshTokenUseCase.execute(new StaffRefreshTokenRequest(refreshToken), presenter);
		return presenter.getResponseEntity();
	}

}
