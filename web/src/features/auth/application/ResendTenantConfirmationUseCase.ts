/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

import type {AuthRepository} from '../domain/AuthRepository';

export class ResendTenantConfirmationUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	/** Resolves with a status message from the backend. */
	execute(tenantId: string): Promise<string> {
		if (!tenantId.trim()) {
			return Promise.reject(new Error('Missing tenant reference — please register again.'));
		}
		return this.repository.resendTenantConfirmation(tenantId.trim());
	}
}
