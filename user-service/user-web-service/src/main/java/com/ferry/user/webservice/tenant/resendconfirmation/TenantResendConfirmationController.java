package com.ferry.user.webservice.tenant.resendconfirmation;

import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationRequest;
import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class TenantResendConfirmationController{
	private final TenantResendConfirmationUseCase tenantResendConfirmationUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping("/auth/tenant/resendConfirmation")
	public ResponseEntity<?> resendConfirmation(@RequestBody TenantResendConfirmationRequest request){
		WebTenantResendConfirmationPresenter presenter = new WebTenantResendConfirmationPresenter();
		tenantResendConfirmationUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
