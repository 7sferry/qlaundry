/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {Page} from '@/core/pagination/Pagination';
import type {MachineFilters, MachineRepository} from '../domain/MachineRepository';
import type {CreateMachineInput, Machine, MachineType, UpdateMachineInput} from '../domain/Machine';
import {MACHINE_ENDPOINTS} from './machineEndpoints';

interface MachineApiItem {
	id: string;
	name: string;
	type: string;
	description: string | null;
	active: boolean;
	inUse?: boolean;
	currentOrderNumber?: string | null;
}

interface MachineListApiResponse {
	machines: MachineApiItem[];
	nextCursor: string | null;
	prevCursor: string | null;
}

function toMachine(item: MachineApiItem): Machine {
	return {
		id: item.id,
		name: item.name,
		type: item.type.toLowerCase() as MachineType,
		description: item.description ?? '',
		active: item.active,
		inUse: item.inUse ?? false,
		currentOrderNumber: item.currentOrderNumber ?? null,
	};
}

function buildMachineQuery(filters?: MachineFilters): string {
	const params = new URLSearchParams();
	if (filters?.search) params.set('name', filters.search);
	if (filters?.type) params.set('type', filters.type.toUpperCase());
	params.set('activeOnly', String(filters?.activeOnly ?? false));
	if (filters?.after) params.set('after', filters.after);
	if (filters?.before) params.set('before', filters.before);
	if (filters?.sortBy) params.set('sortBy', filters.sortBy.toUpperCase());
	if (filters?.sortDir) params.set('sortDir', filters.sortDir.toUpperCase());
	if (filters?.pageSize) params.set('pageSize', String(filters.pageSize));
	return `?${params.toString()}`;
}

export class MachineRepositoryImpl implements MachineRepository {
	async getMachines(filters?: MachineFilters): Promise<Page<Machine>> {
		const res = await httpClient.get<MachineListApiResponse>(`${MACHINE_ENDPOINTS.list}${buildMachineQuery(filters)}`);
		return {items: res.machines.map(toMachine), nextCursor: res.nextCursor, prevCursor: res.prevCursor};
	}

	async createMachine(input: CreateMachineInput): Promise<Machine> {
		const res = await httpClient.post<MachineApiItem>(MACHINE_ENDPOINTS.create, {
			name: input.name,
			type: input.type.toUpperCase(),
			description: input.description,
		});
		return toMachine(res);
	}

	async updateMachine(input: UpdateMachineInput): Promise<Machine> {
		const res = await httpClient.put<MachineApiItem>(MACHINE_ENDPOINTS.update, {
			machineId: input.id,
			name: input.name,
			type: input.type.toUpperCase(),
			description: input.description,
			active: input.active,
		});
		return toMachine(res);
	}
}

export const machineRepository = new MachineRepositoryImpl();
