package com.ferry.user.core.tools;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PasswordSubjectGateway{
	Optional<PasswordSubject> findByStaffId(String staffId);

	Optional<PasswordSubject> findByTenantId(String tenantId);
}
