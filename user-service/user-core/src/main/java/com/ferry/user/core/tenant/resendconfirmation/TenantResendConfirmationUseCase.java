package com.ferry.user.core.tenant.resendconfirmation;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface TenantResendConfirmationUseCase{
	void execute(TenantResendConfirmationRequest request, TenantResendConfirmationPresenter presenter);
}
