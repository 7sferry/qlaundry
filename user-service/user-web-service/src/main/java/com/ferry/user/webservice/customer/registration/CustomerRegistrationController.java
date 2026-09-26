package com.ferry.user.webservice.customer.registration;

import com.ferry.user.core.customer.registration.CustomerRegistrationRequest;
import com.ferry.user.core.customer.registration.CustomerRegistrationUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerRegistrationController{
	private final CustomerRegistrationUseCase customerRegistrationUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping("/customer/registration")
	public ResponseEntity<?> register(@RequestBody CustomerRegistrationRequest request,
	                                  @AuthenticationPrincipal UserAuthPrincipal principal){
		WebCustomerRegistrationPresenter presenter = new WebCustomerRegistrationPresenter();
		customerRegistrationUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
