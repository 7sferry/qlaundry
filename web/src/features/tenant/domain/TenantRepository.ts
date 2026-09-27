/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

import type {Tenant, UpdateTenantInput} from './Tenant';

export interface TenantRepository {
	getTenant(): Promise<Tenant>;

	updateTenant(input: UpdateTenantInput): Promise<Tenant>;
}
