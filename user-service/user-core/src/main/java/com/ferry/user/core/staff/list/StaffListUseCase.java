package com.ferry.user.core.staff.list;

import com.ferry.user.domain.token.UserPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffListUseCase{
	void execute(StaffListRequest request, UserPrincipal principal, StaffListPresenter presenter);
}
