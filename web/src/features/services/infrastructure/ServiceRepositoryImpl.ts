/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {Page} from '@/core/pagination/Pagination';
import type {ServiceFilters, ServiceRepository} from '../domain/ServiceRepository';
import type {
	CreateServiceInput,
	LaundryService,
	ServiceCategory,
	ServiceStats,
	ServiceUnit,
	UpdateServiceInput,
} from '../domain/Service';
import {SERVICE_ENDPOINTS} from './serviceEndpoints';

interface ServiceApiItem {
	id: string;
	name: string;
	description: string | null;
	pricePerUnit: number;
	unit: string;
	category: string;
	estimatedHours: number;
	expressMultiplier: number;
	popular: boolean;
	active: boolean;
}

interface ServiceListApiResponse {
	services: ServiceApiItem[];
	nextCursor: string | null;
	prevCursor: string | null;
}

function toService(item: ServiceApiItem): LaundryService {
	return {
		id: item.id,
		name: item.name,
		description: item.description ?? '',
		pricePerUnit: item.pricePerUnit,
		unit: item.unit.toLowerCase() as ServiceUnit,
		estimatedHours: item.estimatedHours,
		expressMultiplier: item.expressMultiplier,
		popular: item.popular,
		active: item.active,
		category: item.category.toLowerCase() as ServiceCategory,
	};
}

/** The management page needs inactive services too, so it defaults `activeOnly` off unlike the order-create picker. */
function buildServiceQuery(filters?: ServiceFilters): string {
	const params = new URLSearchParams();
	if (filters?.search) params.set('name', filters.search);
	if (filters?.category) params.set('category', filters.category.toUpperCase());
	params.set('activeOnly', String(filters?.activeOnly ?? false));
	if (filters?.after) params.set('after', filters.after);
	if (filters?.before) params.set('before', filters.before);
	if (filters?.sortBy) params.set('sortBy', filters.sortBy.toUpperCase());
	if (filters?.sortDir) params.set('sortDir', filters.sortDir.toUpperCase());
	if (filters?.pageSize) params.set('pageSize', String(filters.pageSize));
	return `?${params.toString()}`;
}

export class ServiceRepositoryImpl implements ServiceRepository {
	async getServices(filters?: ServiceFilters): Promise<Page<LaundryService>> {
		const res = await httpClient.get<ServiceListApiResponse>(`${SERVICE_ENDPOINTS.list}${buildServiceQuery(filters)}`);
		return {items: res.services.map(toService), nextCursor: res.nextCursor, prevCursor: res.prevCursor};
	}

	async createService(input: CreateServiceInput): Promise<LaundryService> {
		const res = await httpClient.post<ServiceApiItem>(SERVICE_ENDPOINTS.create, {
			name: input.name,
			description: input.description,
			pricePerUnit: input.pricePerUnit,
			unit: input.unit.toUpperCase(),
			category: input.category.toUpperCase(),
			estimatedHours: input.estimatedHours,
			expressMultiplier: input.expressMultiplier,
			popular: input.popular,
		});
		return toService(res);
	}

	async updateService(input: UpdateServiceInput): Promise<LaundryService> {
		const res = await httpClient.put<ServiceApiItem>(SERVICE_ENDPOINTS.update, {
			serviceId: input.id,
			name: input.name,
			description: input.description,
			pricePerUnit: input.pricePerUnit,
			unit: input.unit.toUpperCase(),
			category: input.category.toUpperCase(),
			estimatedHours: input.estimatedHours,
			expressMultiplier: input.expressMultiplier,
			popular: input.popular,
			active: input.active,
		});
		return toService(res);
	}

	async deleteService(id: string): Promise<void> {
		await httpClient.delete(`${SERVICE_ENDPOINTS.delete}?serviceId=${encodeURIComponent(id)}`);
	}

	async getServiceStats(): Promise<ServiceStats> {
		return httpClient.get<ServiceStats>(SERVICE_ENDPOINTS.stats);
	}
}

export const serviceRepository = new ServiceRepositoryImpl();
