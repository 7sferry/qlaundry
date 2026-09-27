package com.ferry.user.webservice.tenant.detail;

import com.ferry.user.core.tenant.detail.TenantDetailUseCase;
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
 * on September 2026     *
 ************************/

@RestController
@RequiredArgsConstructor
public class TenantDetailController{
	private final TenantDetailUseCase tenantDetailUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/tenant/detail")
	public ResponseEntity<?> getDetail(@AuthenticationPrincipal UserAuthPrincipal principal){
		WebTenantDetailPresenter presenter = new WebTenantDetailPresenter();
		tenantDetailUseCase.execute(principal, presenter);
		return presenter.getResponseEntity();
	}

}
