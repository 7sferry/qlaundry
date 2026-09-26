package com.ferry.user.webservice.customer.update;

import com.ferry.user.core.customer.update.CustomerUpdateRequest;
import com.ferry.user.core.customer.update.CustomerUpdateUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerUpdateController{
	private final CustomerUpdateUseCase customerUpdateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PutMapping("/customer/update")
	public ResponseEntity<?> update(@RequestBody CustomerUpdateRequest request,
	                                @AuthenticationPrincipal UserAuthPrincipal principal){
		WebCustomerUpdatePresenter presenter = new WebCustomerUpdatePresenter();
		customerUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
