package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffDeleteUseCase implements StaffDeleteUseCase{
	private final StaffDeleteGateway gateway;

	@Override
	public void execute(StaffDeleteRequest request, UserAuthPrincipal principal, StaffDeletePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can delete staff");
		}
		request.validate();
		Username username = new Username(request.username());
		TenantId tenantId = new TenantId(principal.tenantId());
		Staff target = gateway.findByUsername(username, tenantId)
				.orElseThrow(() -> new NotFoundException("Staff Not Found"));
		if(target.id().equals(principal.userId())){
			throw new ForbiddenActionException("Cannot delete your own account");
		}
		gateway.save(target.markDeleted(principal.userId()));
		presenter.present(new StaffDeleteResponse(target.usernameValue()));
	}

}
