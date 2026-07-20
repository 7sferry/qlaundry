/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import type {StaffFilters, StaffRepository} from '../domain/StaffRepository';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../domain/Staff';
import {fallbackStaff} from './staffFallbackData';

let localStaff: Staff[] = [...fallbackStaff];

interface StaffListApiResponse {
	staffs: {
		description: string | null;
		fullName: string;
		createdAt: number;
		username: string;
		emails: { email: string }[];
		phones: { phone: string }[];
		addresses: { address: string }[];
	}[];
}

function toStaff(item: StaffListApiResponse['staffs'][number]): Staff {
	return {
		id: item.username,
		username: item.username,
		fullName: item.fullName,
		description: item.description ?? undefined,
		emails: item.emails.map((e) => e.email),
		phones: item.phones.map((p) => p.phone),
		addresses: item.addresses.map((a) => a.address),
		joinedAt: new Date(item.createdAt).toISOString(),
	};
}

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
		const query = filters?.search ? `?fullName=${encodeURIComponent(filters.search)}` : '';
		const res = await httpClient.get<StaffListApiResponse>(`/staff/list${query}`);
		return res.staffs.map(toStaff);
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
		await httpClient.post<{ username: string }>('/auth/staff/registration', {
			username: input.username,
			password: input.password,
			fullName: input.fullName,
			description: input.description,
			emails: input.emails,
			phones: input.phones,
			addresses: input.addresses,
		});
		const staff = newStaffFromInput(input);
		localStaff = [staff, ...localStaff];
		return staff;
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
