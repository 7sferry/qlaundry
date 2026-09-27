package com.ferry.user.webservice.tenant.update;

import com.ferry.user.core.tenant.update.TenantUpdateRequest;
import com.ferry.user.core.tenant.update.TenantUpdateUseCase;
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
 * on September 2026     *
 ************************/

@RestController
@RequiredArgsConstructor
public class TenantUpdateController{
	private final TenantUpdateUseCase tenantUpdateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PutMapping("/tenant/update")
	public ResponseEntity<?> updateTenant(@RequestBody TenantUpdateRequest request,
	                                      @AuthenticationPrincipal UserAuthPrincipal principal){
		WebTenantUpdatePresenter presenter = new WebTenantUpdatePresenter();
		tenantUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
