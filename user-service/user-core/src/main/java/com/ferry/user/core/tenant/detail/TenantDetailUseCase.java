package com.ferry.user.core.tenant.detail;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

public interface TenantDetailUseCase{
	void execute(UserAuthPrincipal principal, TenantDetailPresenter presenter);
}
