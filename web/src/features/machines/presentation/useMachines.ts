/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {CreateMachineInput, Machine, UpdateMachineInput} from '../domain/Machine';
import type {MachineFilters} from '../domain/MachineRepository';
import {machineUseCases} from '../application/MachineUseCases';
import {machineRepository} from '../infrastructure/MachineRepositoryImpl';

const useCases = machineUseCases(machineRepository);

export const DEFAULT_MACHINE_FILTERS: MachineFilters = {sortBy: 'name', sortDir: 'asc'};

export function useMachines() {
	const fetchMachinePage = useCallback(
			(filters?: MachineFilters) => useCases.listMachines(filters ?? DEFAULT_MACHINE_FILTERS),
			[],
	);
	const {
		items: machines, setItems: setMachines, loading, error, hasNext, hasPrev, refresh, reload, goNext, goPrevious,
	} = usePaginatedList<Machine, MachineFilters>(fetchMachinePage);

	const createMachine = useCallback(async (input: CreateMachineInput): Promise<Machine> => {
		const created = await useCases.createMachine(input);
		await reload(true);
		return created;
	}, [reload]);

	const updateMachine = useCallback(async (input: UpdateMachineInput): Promise<Machine> => {
		const updated = await useCases.updateMachine(input);
		setMachines((prev) => prev.map((m) => (
				m.id === updated.id ? {...updated, inUse: m.inUse, currentOrderNumber: m.currentOrderNumber} : m
		)));
		return updated;
	}, [setMachines]);

	return {
		machines,
		loading,
		error,
		hasNext,
		hasPrev,
		refresh,
		goNext,
		goPrevious,
		createMachine,
		updateMachine,
	};
}
