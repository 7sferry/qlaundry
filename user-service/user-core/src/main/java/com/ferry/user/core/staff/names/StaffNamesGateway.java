package com.ferry.user.core.staff.names;

import com.ferry.user.domain.staff.names.StaffNameProjection;
import com.ferry.user.domain.tenant.TenantId;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface StaffNamesGateway{
	List<StaffNameProjection> findNames(TenantId tenantId, Set<String> staffIds);
}
