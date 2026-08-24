/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {ServiceFilters, ServiceRepository} from '../domain/ServiceRepository';
import type {CreateServiceInput, LaundryService, ServiceCategory, ServiceUnit, UpdateServiceInput} from '../domain/Service';

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
	return `?${params.toString()}`;
}

export class ServiceRepositoryImpl implements ServiceRepository {
	async getServices(filters?: ServiceFilters): Promise<LaundryService[]> {
		const res = await httpClient.get<ServiceListApiResponse>(`/service/list${buildServiceQuery(filters)}`);
		return res.services.map(toService);
	}

	async createService(input: CreateServiceInput): Promise<LaundryService> {
		const res = await httpClient.post<ServiceApiItem>('/service/create', {
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
		const res = await httpClient.put<ServiceApiItem>('/service/update', {
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
		await httpClient.delete(`/service/delete?serviceId=${encodeURIComponent(id)}`);
	}
}

export const serviceRepository = new ServiceRepositoryImpl();
