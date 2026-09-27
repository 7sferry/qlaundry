/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

export interface Tenant {
	tenantName: string;
	description?: string;
	timeZone: string;
}

export interface UpdateTenantInput {
	tenantName: string;
	description?: string;
	timeZone: string;
}
