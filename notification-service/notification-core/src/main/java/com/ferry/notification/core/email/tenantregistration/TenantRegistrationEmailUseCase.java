package com.ferry.notification.core.email.tenantregistration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantRegistrationEmailUseCase{
	void execute(TenantRegistrationEmailRequest request, TenantRegistrationEmailPresenter presenter);
}
