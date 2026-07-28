import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import type {Staff, UpdateStaffInput} from '../domain/Staff';
import {UpdateStaffUseCase} from './UpdateStaffUseCase';

const mockStaff: Staff = {
	id: 'staff_1',
	username: 'ratna.dewi',
	fullName: 'Ratna D.',
	emails: ['ratna@qlaundry.id'],
	phones: ['081234567801'],
	addresses: ['Jl. Anggrek No. 3'],
	joinedAt: '2025-02-10',
};

function makeRepo(updateStaffImpl = vi.fn().mockResolvedValue(mockStaff)): StaffRepository {
	return {
		getStaffList: vi.fn(),
		getStaffById: vi.fn(),
		createStaff: vi.fn(),
		updateStaff: updateStaffImpl,
		deleteStaff: vi.fn(),
	} as unknown as StaffRepository;
}

describe('UpdateStaffUseCase', () => {
	it('rejects when id is empty', async () => {
		const useCase = new UpdateStaffUseCase(makeRepo());
		await expect(useCase.execute({id: '', fullName: 'Ratna D.'}))
				.rejects.toThrow('ID staf wajib diisi.');
	});

	it('delegates to the repository with valid input', async () => {
		const updateStaffFn = vi.fn().mockResolvedValue(mockStaff);
		const useCase = new UpdateStaffUseCase(makeRepo(updateStaffFn));
		const input: UpdateStaffInput = {id: 'staff_1', fullName: 'Ratna D.'};

		const result = await useCase.execute(input);

		expect(updateStaffFn).toHaveBeenCalledWith(input);
		expect(result).toBe(mockStaff);
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Staff not found');
		const useCase = new UpdateStaffUseCase(makeRepo(vi.fn().mockRejectedValue(repoError)));

		await expect(useCase.execute({id: 'staff_1'})).rejects.toThrow('Staff not found');
	});
});
