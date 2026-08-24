package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerRegistrationUseCase{
	void execute(CustomerRegistrationRequest request, UserAuthPrincipal principal, CustomerRegistrationPresenter presenter);
}
