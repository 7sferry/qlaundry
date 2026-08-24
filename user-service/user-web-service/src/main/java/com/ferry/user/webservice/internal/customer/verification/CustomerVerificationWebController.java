package com.ferry.user.webservice.internal.customer.verification;

import com.ferry.user.core.customer.verification.CustomerVerificationRequest;
import com.ferry.user.core.customer.verification.CustomerVerificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerVerificationWebController{
	private final CustomerVerificationUseCase customerVerificationUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/internal/customer/verification")
	public ResponseEntity<?> verify(CustomerVerificationRequest request){
		CustomerVerificationWebPresenter presenter = new CustomerVerificationWebPresenter();
		customerVerificationUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
