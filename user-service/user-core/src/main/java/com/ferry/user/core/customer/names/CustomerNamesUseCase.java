package com.ferry.user.core.customer.names;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerNamesUseCase{
	void execute(CustomerNamesRequest request, UserAuthPrincipal principal, CustomerNamesPresenter presenter);
}
