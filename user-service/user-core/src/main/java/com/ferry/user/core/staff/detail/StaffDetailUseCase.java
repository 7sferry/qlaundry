package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.token.UserPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDetailUseCase{
	void execute(StaffDetailRequest request, UserPrincipal principal, StaffDetailPresenter presenter);
}
