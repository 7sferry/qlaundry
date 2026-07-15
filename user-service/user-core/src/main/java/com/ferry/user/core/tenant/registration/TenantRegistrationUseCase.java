package com.ferry.user.core.tenant.registration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantRegistrationUseCase{
	void execute(TenantRegistrationRequest request, TenantRegistrationPresenter presenter);
}
