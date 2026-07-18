package com.ferry.user.webservice.staff.login;

import com.ferry.user.core.staff.login.StaffLoginRequest;
import com.ferry.user.core.staff.login.StaffLoginResponse;
import com.ferry.user.core.staff.login.StaffLoginUseCase;
import com.ferry.user.core.tools.TokenProcessor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffLoginWebController{
	private final StaffLoginUseCase staffLoginUseCase;
	private final TokenProcessor tokenProcessor;

	@Transactional
	@PostMapping("/auth/staff/login")
	public ResponseEntity<?> staffLogin(@RequestBody StaffLoginRequest staffLoginRequest, HttpServletResponse servletResponse){
		StaffLoginWebPresenter presenter = new StaffLoginWebPresenter(servletResponse, tokenProcessor);
		staffLoginUseCase.execute(staffLoginRequest, presenter);
		return presenter.getResponseEntity();
	}

}
