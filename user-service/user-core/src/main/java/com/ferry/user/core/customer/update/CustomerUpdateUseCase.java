package com.ferry.user.core.customer.update;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerUpdateUseCase{
	void execute(CustomerUpdateRequest request, UserAuthPrincipal principal, CustomerUpdatePresenter presenter);
}
