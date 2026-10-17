/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import type {MachineFilters, MachineRepository} from '../domain/MachineRepository';
import type {CreateMachineInput, Machine, UpdateMachineInput} from '../domain/Machine';

function validate(input: CreateMachineInput): void {
	if (!input.name.trim()) throw new Error('Machine name is required.');
}

export const machineUseCases = (repository: MachineRepository) => ({
	listMachines: (filters?: MachineFilters) => repository.getMachines(filters),
	createMachine: (input: CreateMachineInput): Promise<Machine> => {
		validate(input);
		return repository.createMachine(input);
	},
	updateMachine: (input: UpdateMachineInput): Promise<Machine> => {
		validate(input);
		return repository.updateMachine(input);
	},
});
