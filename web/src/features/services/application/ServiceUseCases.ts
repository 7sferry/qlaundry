/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {ServiceFilters, ServiceRepository} from '../domain/ServiceRepository';
import type {CreateServiceInput, LaundryService, UpdateServiceInput} from '../domain/Service';

function validate(input: CreateServiceInput): void {
	if (!input.name.trim()) throw new Error('Service name is required.');
	if (!(input.pricePerUnit > 0)) throw new Error('Price per unit must be greater than zero.');
	if (!(input.estimatedHours > 0)) throw new Error('Estimated hours must be greater than zero.');
	if (input.express) {
		const {hours, multiplier} = input.express;
		if (!Number.isInteger(hours) || hours < 1 || hours > input.estimatedHours) {
			throw new Error('Express hours must be a whole number between 1 and the estimated hours.');
		}
		if (!(multiplier >= 1)) throw new Error('Express multiplier must be at least 1.');
	}
}

export const serviceUseCases = (repository: ServiceRepository) => ({
	listServices: (filters?: ServiceFilters) => repository.getServices(filters),
	createService: (input: CreateServiceInput): Promise<LaundryService> => {
		validate(input);
		return repository.createService(input);
	},
	updateService: (input: UpdateServiceInput): Promise<LaundryService> => {
		validate(input);
		return repository.updateService(input);
	},
	deleteService: (id: string) => repository.deleteService(id),
	getStats: () => repository.getServiceStats(),
});
