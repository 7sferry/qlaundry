/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export type MachineType = 'washer' | 'dryer' | 'ironer';

export interface MachineOption {
	id: string;
	name: string;
	type: MachineType;
	capacityKg: number | null;
	inUse: boolean;
	currentOrderNumber: string | null;
}

export interface OrderMachine {
	machineId: string;
	name: string;
	type: MachineType;
}

export const MACHINE_TYPE_LABELS: Record<MachineType, string> = {
	washer: 'Washer',
	dryer: 'Dryer',
	ironer: 'Ironer',
};
