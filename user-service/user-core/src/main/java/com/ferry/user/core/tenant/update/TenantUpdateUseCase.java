package com.ferry.user.core.tenant.update;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

public interface TenantUpdateUseCase{
	void execute(TenantUpdateRequest request, UserAuthPrincipal principal, TenantUpdatePresenter presenter);
}
