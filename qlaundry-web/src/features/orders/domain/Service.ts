/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export type ServiceUnit = 'kg' | 'item' | 'load' | 'set';
export type ServiceCategory = 'wash' | 'dry_clean' | 'iron' | 'specialty';

export interface LaundryService {
	id: string;
	name: string;
	description: string;
	pricePerUnit: number;
	unit: ServiceUnit;
	estimatedHours: number;
	expressMultiplier: number;
	popular: boolean;
	active: boolean;
	category: ServiceCategory;
}
