package com.ferry.user.webservice.tenant.confirmregistration;

import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationRequest;
import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class TenantConfirmRegistrationWebController{
	private final TenantConfirmRegistrationUseCase tenantConfirmRegistrationUseCase;

	@Transactional
	@GetMapping("/auth/tenant/confirmRegistration")
	public ResponseEntity<?> confirmRegistration(TenantConfirmRegistrationRequest request){
		TenantConfirmRegistrationWebPresenter presenter = new TenantConfirmRegistrationWebPresenter();
		tenantConfirmRegistrationUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
