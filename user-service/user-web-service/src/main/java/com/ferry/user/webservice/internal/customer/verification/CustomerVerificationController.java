package com.ferry.user.webservice.internal.customer.verification;

import com.ferry.user.core.customer.verification.CustomerVerificationRequest;
import com.ferry.user.core.customer.verification.CustomerVerificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerVerificationController{
	private final CustomerVerificationUseCase customerVerificationUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/internal/customer/verification")
	public ResponseEntity<?> verify(CustomerVerificationRequest request){
		WebCustomerVerificationPresenter presenter = new WebCustomerVerificationPresenter();
		customerVerificationUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
