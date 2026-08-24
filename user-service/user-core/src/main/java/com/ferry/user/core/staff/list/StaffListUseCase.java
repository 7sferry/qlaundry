package com.ferry.user.core.staff.list;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffListUseCase{
	void execute(StaffListRequest request, UserAuthPrincipal principal, StaffListPresenter presenter);
}
