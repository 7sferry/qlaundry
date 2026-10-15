package com.ferry.user.webservice.customer.names;

import com.ferry.user.core.customer.names.CustomerNamesRequest;
import com.ferry.user.core.customer.names.CustomerNamesUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerNamesController{
	private final CustomerNamesUseCase customerNamesUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/customer/names")
	public ResponseEntity<?> getNames(CustomerNamesRequest request, @AuthenticationPrincipal UserAuthPrincipal principal){
		WebCustomerNamesPresenter presenter = new WebCustomerNamesPresenter();
		customerNamesUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
