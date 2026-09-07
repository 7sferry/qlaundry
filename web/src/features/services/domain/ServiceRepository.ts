/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreateServiceInput, LaundryService, ServiceCategory, UpdateServiceInput} from './Service';

export type ServiceSortBy = 'id' | 'name';

export interface ServiceFilters extends PaginationParams<ServiceSortBy> {
	search?: string;
	category?: ServiceCategory;
	activeOnly?: boolean;
}

export interface ServiceRepository {
	getServices(filters?: ServiceFilters): Promise<Page<LaundryService>>;

	createService(input: CreateServiceInput): Promise<LaundryService>;

	updateService(input: UpdateServiceInput): Promise<LaundryService>;

	deleteService(id: string): Promise<void>;
}
