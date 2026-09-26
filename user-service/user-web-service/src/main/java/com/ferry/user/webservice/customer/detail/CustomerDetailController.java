package com.ferry.user.webservice.customer.detail;

import com.ferry.user.core.customer.detail.CustomerDetailRequest;
import com.ferry.user.core.customer.detail.CustomerDetailUseCase;
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
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerDetailController{
	private final CustomerDetailUseCase customerDetailUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/customer/detail")
	public ResponseEntity<?> getDetail(CustomerDetailRequest request, @AuthenticationPrincipal UserAuthPrincipal principal){
		WebCustomerDetailPresenter presenter = new WebCustomerDetailPresenter();
		customerDetailUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
