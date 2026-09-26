package com.ferry.user.webservice.customer.delete;

import com.ferry.user.core.customer.delete.CustomerDeleteRequest;
import com.ferry.user.core.customer.delete.CustomerDeleteUseCase;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerDeleteController{
	private final CustomerDeleteUseCase customerDeleteUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@DeleteMapping("/customer/delete")
	public ResponseEntity<?> delete(CustomerDeleteRequest request, @AuthenticationPrincipal UserAuthPrincipal principal){
		WebCustomerDeletePresenter presenter = new WebCustomerDeletePresenter();
		customerDeleteUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
