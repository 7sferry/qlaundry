package com.ferry.user.core.customer.delete;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerDeleteUseCase{
	void execute(CustomerDeleteRequest request, UserAuthPrincipal principal, CustomerDeletePresenter presenter);
}
