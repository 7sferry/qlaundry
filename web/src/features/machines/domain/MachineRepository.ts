/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreateMachineInput, Machine, MachineType, UpdateMachineInput} from './Machine';

export type MachineSortBy = 'id' | 'name';

export interface MachineFilters extends PaginationParams<MachineSortBy> {
	search?: string;
	type?: MachineType;
	activeOnly?: boolean;
}

export interface MachineRepository {
	getMachines(filters?: MachineFilters): Promise<Page<Machine>>;

	createMachine(input: CreateMachineInput): Promise<Machine>;

	updateMachine(input: UpdateMachineInput): Promise<Machine>;
}
