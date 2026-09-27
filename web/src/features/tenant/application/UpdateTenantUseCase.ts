/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

import type {TenantRepository} from '../domain/TenantRepository';
import type {Tenant, UpdateTenantInput} from '../domain/Tenant';

export class UpdateTenantUseCase {
	private readonly repository: TenantRepository;

	constructor(repository: TenantRepository) {
		this.repository = repository;
	}

	execute(input: UpdateTenantInput): Promise<Tenant> {
		if (!input.tenantName.trim()) {
			return Promise.reject(new Error('Nama tenant wajib diisi.'));
		}
		if (!input.timeZone.trim()) {
			return Promise.reject(new Error('Zona waktu wajib diisi.'));
		}
		return this.repository.updateTenant(input);
	}
}
