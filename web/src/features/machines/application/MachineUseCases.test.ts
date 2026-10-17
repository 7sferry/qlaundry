/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {describe, expect, it, vi} from 'vitest';
import type {MachineFilters, MachineRepository} from '../domain/MachineRepository';
import type {CreateMachineInput, Machine, UpdateMachineInput} from '../domain/Machine';
import {machineUseCases} from './MachineUseCases';

const mockMachine: Machine = {
	id: 'mch_washer_1',
	name: 'Mesin Cuci 1',
	type: 'washer',
	description: 'Front loader',
	active: true,
	inUse: false,
	currentOrderNumber: null,
};

const mockMachinePage = {items: [mockMachine], nextCursor: null, prevCursor: null};

function makeRepo(): { repo: MachineRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getMachines: vi.fn().mockResolvedValue(mockMachinePage),
		createMachine: vi.fn().mockResolvedValue(mockMachine),
		updateMachine: vi.fn().mockResolvedValue(mockMachine),
	};
	return {repo: fns as unknown as MachineRepository, fns};
}

describe('machineUseCases', () => {
	it('listMachines passes the filters through to repository.getMachines', async () => {
		const {repo, fns} = makeRepo();
		const useCases = machineUseCases(repo);
		const filters: MachineFilters = {search: 'mesin', type: 'dryer', activeOnly: true, sortBy: 'name', sortDir: 'asc'};

		const result = await useCases.listMachines(filters);

		expect(fns.getMachines).toHaveBeenCalledWith(filters);
		expect(result).toEqual(mockMachinePage);
	});

	it('createMachine delegates a valid input to repository.createMachine', async () => {
		const {repo, fns} = makeRepo();
		const useCases = machineUseCases(repo);
		const input: CreateMachineInput = {name: 'Pengering 2', type: 'dryer'};

		const result = await useCases.createMachine(input);

		expect(fns.createMachine).toHaveBeenCalledWith(input);
		expect(result).toBe(mockMachine);
	});

	it('createMachine rejects a blank name without calling the repository', () => {
		const {repo, fns} = makeRepo();
		const useCases = machineUseCases(repo);

		expect(() => useCases.createMachine({name: '   ', type: 'washer'})).toThrow('Machine name is required.');
		expect(fns.createMachine).not.toHaveBeenCalled();
	});

	it('updateMachine delegates a valid input to repository.updateMachine', async () => {
		const {repo, fns} = makeRepo();
		const useCases = machineUseCases(repo);
		const input: UpdateMachineInput = {id: 'mch_washer_1', name: 'Mesin Cuci 1', type: 'washer', active: false};

		await useCases.updateMachine(input);

		expect(fns.updateMachine).toHaveBeenCalledWith(input);
	});
});
