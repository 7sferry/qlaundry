package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDeleteGateway{
	Optional<Staff> findByUsername(Username username, TenantId tenantId);
	void save(Staff staff);
}
