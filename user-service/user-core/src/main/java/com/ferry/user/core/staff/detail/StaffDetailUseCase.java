package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDetailUseCase{
	void execute(StaffDetailRequest request, UserAuthPrincipal principal, StaffDetailPresenter presenter);
}
