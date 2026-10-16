/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export type MachineType = 'washer' | 'dryer' | 'ironer';

export interface Machine {
	id: string;
	name: string;
	type: MachineType;
	capacityKg: number | null;
	description: string;
	active: boolean;
	inUse: boolean;
	currentOrderNumber: string | null;
}

export interface CreateMachineInput {
	name: string;
	type: MachineType;
	capacityKg: number | null;
	description?: string;
}

export interface UpdateMachineInput extends CreateMachineInput {
	id: string;
	active: boolean;
}

export const MACHINE_TYPE_LABELS: Record<MachineType, string> = {
	washer: 'Washer',
	dryer: 'Dryer',
	ironer: 'Ironer',
};
