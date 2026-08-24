package com.ferry.order.webservice.service.delete;

import com.ferry.order.core.service.delete.LaundryServiceDeleteRequest;
import com.ferry.order.core.service.delete.LaundryServiceDeleteUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class LaundryServiceDeleteWebController{
	private final LaundryServiceDeleteUseCase laundryServiceDeleteUseCase;

	@Transactional
	@DeleteMapping("/service/delete")
	public ResponseEntity<?> delete(LaundryServiceDeleteRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		LaundryServiceDeleteWebPresenter presenter = new LaundryServiceDeleteWebPresenter();
		laundryServiceDeleteUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
