package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerDetailUseCase{
	void execute(CustomerDetailRequest request, UserAuthPrincipal principal, CustomerDetailPresenter presenter);
}
