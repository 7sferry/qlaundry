package com.ferry.user.core.staff.update;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffUpdateUseCase{
	void execute(StaffUpdateRequest request, UserAuthPrincipal principal, StaffUpdatePresenter presenter);
}
