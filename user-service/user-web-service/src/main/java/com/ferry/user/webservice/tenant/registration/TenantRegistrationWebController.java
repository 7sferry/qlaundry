package com.ferry.user.webservice.tenant.registration;

import com.ferry.user.core.tenant.registration.TenantRegistrationRequest;
import com.ferry.user.core.tenant.registration.TenantRegistrationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RestController
@RequiredArgsConstructor
public class TenantRegistrationWebController{
	private final TenantRegistrationUseCase tenantRegistrationUseCase;

	@Transactional
	@PostMapping("/auth/tenant/registration")
	public ResponseEntity<?> register(@RequestBody TenantRegistrationRequest request){
		TenantRegistrationWebPresenter presenter = new TenantRegistrationWebPresenter();
		tenantRegistrationUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
