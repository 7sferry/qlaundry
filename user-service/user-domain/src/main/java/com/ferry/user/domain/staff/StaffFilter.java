package com.ferry.user.domain.staff;

import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffFilter(UsernameDomain username, TenantIdDomain tenantId){
}
