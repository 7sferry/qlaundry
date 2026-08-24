/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {CreateServiceInput, LaundryService, ServiceCategory, UpdateServiceInput} from './Service';

export interface ServiceFilters {
	search?: string;
	category?: ServiceCategory;
	activeOnly?: boolean;
}

export interface ServiceRepository {
	getServices(filters?: ServiceFilters): Promise<LaundryService[]>;

	createService(input: CreateServiceInput): Promise<LaundryService>;

	updateService(input: UpdateServiceInput): Promise<LaundryService>;

	deleteService(id: string): Promise<void>;
}
