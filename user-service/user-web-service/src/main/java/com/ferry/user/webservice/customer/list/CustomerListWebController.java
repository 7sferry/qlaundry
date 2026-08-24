package com.ferry.user.webservice.customer.list;

import com.ferry.user.core.customer.list.CustomerListRequest;
import com.ferry.user.core.customer.list.CustomerListUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerListWebController{
	private final CustomerListUseCase customerListUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/customer/list")
	public ResponseEntity<?> getList(CustomerListRequest request, @AuthenticationPrincipal UserAuthPrincipal principal){
		CustomerListWebPresenter presenter = new CustomerListWebPresenter();
		customerListUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
