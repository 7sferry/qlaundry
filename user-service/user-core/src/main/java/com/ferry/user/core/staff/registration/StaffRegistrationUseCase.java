package com.ferry.user.core.staff.registration;

import com.ferry.user.domain.token.UserPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffRegistrationUseCase{
	void execute(StaffRegistrationRequest request, UserPrincipal principal, StaffRegistrationPresenter presenter);
}
