/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {Page} from '@/core/pagination/Pagination';
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
	nextCursor: string | null;
	prevCursor: string | null;
}

function buildStaffListQuery(filters?: StaffFilters): string {
	const params = new URLSearchParams();
	if (filters?.search) params.set('fullName', filters.search);
	if (filters?.cursor) params.set('cursor', filters.cursor);
	if (filters?.direction) params.set('direction', filters.direction.toUpperCase());
	if (filters?.sortBy) params.set('sortBy', filters.sortBy.toUpperCase());
	if (filters?.sortDir) params.set('sortDir', filters.sortDir.toUpperCase());
	const query = params.toString();
	return query ? `?${query}` : '';
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
		id: input.username,
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
	async getStaffList(filters?: StaffFilters): Promise<Page<Staff>> {
		const res = await httpClient.get<StaffListApiResponse>(`/staff/list${buildStaffListQuery(filters)}`);
		return {items: res.staffs.map(toStaff), nextCursor: res.nextCursor, prevCursor: res.prevCursor};
	}

	async getStaffById(id: string): Promise<Staff> {
		const res = await httpClient.get<StaffListApiResponse['staffs'][number]>(
				`/staff/detail?username=${encodeURIComponent(id)}`,
		);
		return toStaff(res);
	}

	async createStaff(input: CreateStaffInput): Promise<Staff> {
		await httpClient.post<{ username: string }>('/staff/registration', {
			username: input.username,
			password: input.password,
			fullName: input.fullName,
			description: input.description,
			role: input.role,
			emails: input.emails,
			phones: input.phones,
			addresses: input.addresses,
		});
		const staff = newStaffFromInput(input);
		localStaff = [staff, ...localStaff];
		return staff;
	}

	async updateStaff(input: UpdateStaffInput): Promise<Staff> {
		const res = await httpClient.put<StaffListApiResponse['staffs'][number]>('/staff/profile', {
			fullName: input.fullName,
			description: input.description,
			currentPassword: input.currentPassword,
			newPassword: input.newPassword,
			emails: input.emails,
			phones: input.phones,
			addresses: input.addresses,
		});
		return toStaff(res);
	}

	async deleteStaff(id: string): Promise<void> {
		await httpClient.delete(`/staff/delete?username=${encodeURIComponent(id)}`);
		localStaff = localStaff.filter((s) => s.id !== id);
	}
}

export const staffRepository = new StaffRepositoryImpl();
