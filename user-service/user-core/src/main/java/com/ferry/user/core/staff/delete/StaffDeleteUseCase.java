package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDeleteUseCase{
	void execute(StaffDeleteRequest request, UserAuthPrincipal principal, StaffDeletePresenter presenter);
}
