package com.ferry.user.core.tenant.expiration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface TenantExpirationUseCase{
	/** Expires tenants still PENDING past the retention window. Returns how many were expired. */
	int execute();
}
