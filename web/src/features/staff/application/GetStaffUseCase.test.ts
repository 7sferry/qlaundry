import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import type {Staff} from '../domain/Staff';
import {GetStaffUseCase} from './GetStaffUseCase';

const mockStaff: Staff = {
	id: 'staff_1',
	username: 'ratna.dewi',
	fullName: 'Ratna Dewi',
	emails: ['ratna@qlaundry.id'],
	phones: ['081234567801'],
	addresses: ['Jl. Anggrek No. 3'],
	joinedAt: '2025-02-10',
};

function makeRepo(getStaffByIdImpl = vi.fn().mockResolvedValue(mockStaff)): StaffRepository {
	return {
		getStaffList: vi.fn(),
		getStaffById: getStaffByIdImpl,
		createStaff: vi.fn(),
		updateStaff: vi.fn(),
		deleteStaff: vi.fn(),
	} as unknown as StaffRepository;
}

describe('GetStaffUseCase', () => {
	it('rejects when id is empty', async () => {
		const useCase = new GetStaffUseCase(makeRepo());
		await expect(useCase.execute('')).rejects.toThrow('ID staf wajib diisi.');
	});

	it('delegates to repository.getStaffById with a valid id', async () => {
		const getStaffByIdFn = vi.fn().mockResolvedValue(mockStaff);
		const useCase = new GetStaffUseCase(makeRepo(getStaffByIdFn));

		const result = await useCase.execute('staff_1');

		expect(getStaffByIdFn).toHaveBeenCalledWith('staff_1');
		expect(result).toBe(mockStaff);
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Staff not found');
		const useCase = new GetStaffUseCase(makeRepo(vi.fn().mockRejectedValue(repoError)));

		await expect(useCase.execute('staff_1')).rejects.toThrow('Staff not found');
	});
});
