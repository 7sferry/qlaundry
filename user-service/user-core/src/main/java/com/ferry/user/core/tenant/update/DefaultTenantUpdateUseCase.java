package com.ferry.user.core.tenant.update;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
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
public class DefaultTenantUpdateUseCase implements TenantUpdateUseCase{
	private final TenantUpdateGateway gateway;

	@Override
	public void execute(TenantUpdateRequest request, UserAuthPrincipal principal, TenantUpdatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can update tenant settings");
		}
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		Tenant tenant = gateway.findByTenantId(tenantId)
				.orElseThrow(() -> new NotFoundException("Tenant Not Found"));
		FullName fullName = new FullName(request.tenantName());
		Description description = new Description(request.description());
		Tenant saved = gateway.save(tenant.update(fullName, description, request.timeZone(), principal.userId()));
		presenter.present(new TenantUpdateResponse(saved));
	}

}
