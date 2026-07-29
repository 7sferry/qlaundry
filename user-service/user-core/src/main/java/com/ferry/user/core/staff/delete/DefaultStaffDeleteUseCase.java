package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffDeleteUseCase implements StaffDeleteUseCase{
	private final StaffDeleteGateway gateway;

	@Override
	public void execute(StaffDeleteRequest request, UserPrincipal principal, StaffDeletePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can delete staff");
		}
		UsernameDomain username = new UsernameDomain(request.username());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		StaffDomain target = gateway.findByUsername(username, tenantId)
				.orElseThrow(() -> new NotFoundException("Staff Not Found"));
		if(target.id().equals(principal.userId())){
			throw new ForbiddenActionException("Cannot delete your own account");
		}
		gateway.save(target.markDeleted(principal.userId()));
		presenter.present(new StaffDeleteResponse(target.usernameValue()));
	}

}
