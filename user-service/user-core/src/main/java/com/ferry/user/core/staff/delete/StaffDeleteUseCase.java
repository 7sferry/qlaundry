package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.token.UserPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDeleteUseCase{
	void execute(StaffDeleteRequest request, UserPrincipal principal, StaffDeletePresenter presenter);
}
