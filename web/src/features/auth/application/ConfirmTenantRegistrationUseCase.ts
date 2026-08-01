/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

import type {AuthRepository} from '../domain/AuthRepository';

export class ConfirmTenantRegistrationUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	execute(tenantId: string, token: string): Promise<void> {
		if (!tenantId.trim() || !token.trim()) {
			return Promise.reject(new Error('This confirmation link is invalid or has expired.'));
		}
		return this.repository.confirmTenantRegistration(tenantId.trim(), token.trim());
	}
}
