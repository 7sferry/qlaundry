/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

import type {TenantRepository} from '../domain/TenantRepository';
import type {Tenant} from '../domain/Tenant';

export class GetTenantUseCase {
	private readonly repository: TenantRepository;

	constructor(repository: TenantRepository) {
		this.repository = repository;
	}

	execute(): Promise<Tenant> {
		return this.repository.getTenant();
	}
}
