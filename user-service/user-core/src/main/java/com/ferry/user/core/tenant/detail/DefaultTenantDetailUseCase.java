package com.ferry.user.core.tenant.detail;

import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantDetailUseCase implements TenantDetailUseCase{
	private final TenantDetailGateway gateway;

	@Override
	public void execute(UserAuthPrincipal principal, TenantDetailPresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can view tenant settings");
		}
		TenantId tenantId = new TenantId(principal.tenantId());
		Tenant tenant = gateway.findByTenantId(tenantId)
				.orElseThrow(() -> new NotFoundException("Tenant Not Found"));
		presenter.present(new TenantDetailResponse(tenant));
	}

}
