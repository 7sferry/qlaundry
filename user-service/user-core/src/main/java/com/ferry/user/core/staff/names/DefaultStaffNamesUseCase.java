package com.ferry.user.core.staff.names;

import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffNamesUseCase implements StaffNamesUseCase{
	private final StaffNamesGateway gateway;

	@Override
	public void execute(StaffNamesRequest request, UserAuthPrincipal principal, StaffNamesPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		Set<String> staffIds = request.sanitizedStaffIds();
		if(staffIds.isEmpty()){
			presenter.present(new StaffNamesResponse(List.of()));
			return;
		}
		presenter.present(new StaffNamesResponse(gateway.findNames(tenantId, staffIds)));
	}

}
