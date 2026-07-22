package com.ferry.notification.core.email.forgottenpassword;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface ForgottenPasswordEmailUseCase{
	void execute(ForgottenPasswordEmailRequest request, ForgottenPasswordEmailPresenter presenter);
}
