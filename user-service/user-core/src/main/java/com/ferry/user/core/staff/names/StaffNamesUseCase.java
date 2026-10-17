package com.ferry.user.core.staff.names;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface StaffNamesUseCase{
	void execute(StaffNamesRequest request, UserAuthPrincipal principal, StaffNamesPresenter presenter);
}
