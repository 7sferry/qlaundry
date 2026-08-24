/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
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

export interface CreateServiceInput {
	name: string;
	description?: string;
	pricePerUnit: number;
	unit: ServiceUnit;
	category: ServiceCategory;
	estimatedHours: number;
	expressMultiplier?: number;
	popular: boolean;
}

export interface UpdateServiceInput extends CreateServiceInput {
	id: string;
	active: boolean;
}

export const SERVICE_UNIT_LABELS: Record<ServiceUnit, string> = {
	kg: 'Per kg',
	item: 'Per item',
	load: 'Per load',
	set: 'Per set',
};

export const SERVICE_CATEGORY_LABELS: Record<ServiceCategory, string> = {
	wash: 'Wash',
	dry_clean: 'Dry Clean',
	iron: 'Iron',
	specialty: 'Specialty',
};
