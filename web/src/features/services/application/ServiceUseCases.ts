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
	if (input.expressMultiplier !== undefined && !(input.expressMultiplier > 0)) {
		throw new Error('Express multiplier must be greater than zero.');
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
});
