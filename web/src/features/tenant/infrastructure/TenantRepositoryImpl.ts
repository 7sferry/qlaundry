/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {TenantRepository} from '../domain/TenantRepository';
import type {Tenant, UpdateTenantInput} from '../domain/Tenant';

interface TenantApiResponse {
	tenantName: string;
	description: string | null;
	timeZone: string;
}

function toTenant(res: TenantApiResponse): Tenant {
	return {
		tenantName: res.tenantName,
		description: res.description ?? undefined,
		timeZone: res.timeZone,
	};
}

export class TenantRepositoryImpl implements TenantRepository {
	async getTenant(): Promise<Tenant> {
		const res = await httpClient.get<TenantApiResponse>('/tenant/detail');
		return toTenant(res);
	}

	async updateTenant(input: UpdateTenantInput): Promise<Tenant> {
		const res = await httpClient.put<TenantApiResponse>('/tenant/update', {
			tenantName: input.tenantName,
			description: input.description || undefined,
			timeZone: input.timeZone,
		});
		return toTenant(res);
	}
}

export const tenantRepository = new TenantRepositoryImpl();
