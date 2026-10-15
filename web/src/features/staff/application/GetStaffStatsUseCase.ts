/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import type {StaffRepository} from '../domain/StaffRepository';
import type {StaffStats} from '../domain/Staff';

export class GetStaffStatsUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(): Promise<StaffStats> {
		return this.repository.getStaffStats();
	}
}
