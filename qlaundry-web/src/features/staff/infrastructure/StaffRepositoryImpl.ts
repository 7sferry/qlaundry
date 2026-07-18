/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import type {StaffFilters, StaffRepository} from '../domain/StaffRepository';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../domain/Staff';
import {fallbackStaff} from './staffFallbackData';

let localStaff: Staff[] = [...fallbackStaff];

function newStaffFromInput(input: CreateStaffInput): Staff {
	return {
		id: `staff-${Date.now()}`,
		username: input.username,
		fullName: input.fullName,
		description: input.description,
		emails: input.emails,
		phones: input.phones,
		addresses: input.addresses,
		joinedAt: new Date().toISOString().split('T')[0],
	};
}

export class StaffRepositoryImpl implements StaffRepository {
	async getStaffList(filters?: StaffFilters): Promise<Staff[]> {
		return withFallback<Staff[]>(
				() => httpClient.get<Staff[]>('/api/staff'),
				() => {
					let result = [...localStaff];
					if (filters?.search) {
						const q = filters.search.toLowerCase();
						result = result.filter(
								(s) =>
										s.fullName.toLowerCase().includes(q) ||
										s.username.toLowerCase().includes(q) ||
										s.phones.some((p) => p.includes(q)) ||
										s.emails.some((e) => e.toLowerCase().includes(q)),
						);
					}
					return result.sort((a, b) => a.fullName.localeCompare(b.fullName));
				},
		);
	}

	async getStaffById(id: string): Promise<Staff> {
		return withFallback<Staff>(
				() => httpClient.get<Staff>(`/api/staff/${id}`),
				() => {
					const s = localStaff.find((x) => x.id === id);
					if (!s) throw new Error(`Staff ${id} not found`);
					return s;
				},
		);
	}

	async createStaff(input: CreateStaffInput): Promise<Staff> {
		const staff = newStaffFromInput(input);
		return withFallback<Staff>(
				async () => {
					await httpClient.post<{ username: string }>('/auth/staff/registration', {
						username: input.username,
						password: input.password,
						fullName: input.fullName,
						description: input.description,
						emails: input.emails,
						phones: input.phones,
						addresses: input.addresses,
					});
					localStaff = [staff, ...localStaff];
					return staff;
				},
				() => {
					localStaff = [staff, ...localStaff];
					return staff;
				},
		);
	}

	async updateStaff(input: UpdateStaffInput): Promise<Staff> {
		return withFallback<Staff>(
				() => httpClient.put<Staff>(`/api/staff/${input.id}`, input),
				() => {
					const idx = localStaff.findIndex((s) => s.id === input.id);
					if (idx === -1) throw new Error(`Staff ${input.id} not found`);
					const updated: Staff = {...localStaff[idx], ...input};
					localStaff = localStaff.map((s, i) => (i === idx ? updated : s));
					return updated;
				},
		);
	}

	async deleteStaff(id: string): Promise<void> {
		await withFallback(
				() => httpClient.delete(`/api/staff/${id}`),
				() => {
					localStaff = localStaff.filter((s) => s.id !== id);
				},
		);
	}
}

export const staffRepository = new StaffRepositoryImpl();
