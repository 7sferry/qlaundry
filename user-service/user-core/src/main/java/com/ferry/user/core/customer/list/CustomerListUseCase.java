package com.ferry.user.core.customer.list;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerListUseCase{
	void execute(CustomerListRequest request, UserAuthPrincipal principal, CustomerListPresenter presenter);
}
