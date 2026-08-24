import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import type {Staff} from '../domain/Staff';
import {ListStaffUseCase} from './ListStaffUseCase';

const mockStaff: Staff = {
	id: 'staff_1',
	username: 'ratna.dewi',
	fullName: 'Ratna Dewi',
	description: 'Kasir shift pagi',
	emails: ['ratna@qlaundry.id'],
	phones: ['081234567801'],
	addresses: ['Jl. Anggrek No. 3'],
	joinedAt: '2025-02-10',
};

const mockPage = {items: [mockStaff], nextCursor: null, prevCursor: null};

function makeRepo(getStaffListImpl = vi.fn().mockResolvedValue(mockPage)): StaffRepository {
	return {
		getStaffList: getStaffListImpl,
		getStaffById: vi.fn(),
		createStaff: vi.fn(),
		updateStaff: vi.fn(),
		deleteStaff: vi.fn(),
	} as unknown as StaffRepository;
}

describe('ListStaffUseCase', () => {
	it('delegates to repository.getStaffList', async () => {
		const useCase = new ListStaffUseCase(makeRepo());

		const result = await useCase.execute();

		expect(result).toEqual(mockPage);
	});

	it('passes filters through to the repository', async () => {
		const getStaffListFn = vi.fn().mockResolvedValue(mockPage);
		const useCase = new ListStaffUseCase(makeRepo(getStaffListFn));
		const filters = {search: 'Ratna'};

		await useCase.execute(filters);

		expect(getStaffListFn).toHaveBeenCalledWith(filters);
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Network error');
		const useCase = new ListStaffUseCase(makeRepo(vi.fn().mockRejectedValue(repoError)));

		await expect(useCase.execute()).rejects.toThrow('Network error');
	});
});
