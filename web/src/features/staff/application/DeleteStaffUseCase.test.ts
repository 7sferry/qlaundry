import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import {DeleteStaffUseCase} from './DeleteStaffUseCase';

function makeRepo(deleteStaffImpl = vi.fn().mockResolvedValue(undefined)): StaffRepository {
	return {
		getStaffList: vi.fn(),
		getStaffById: vi.fn(),
		createStaff: vi.fn(),
		updateStaff: vi.fn(),
		deleteStaff: deleteStaffImpl,
	} as unknown as StaffRepository;
}

describe('DeleteStaffUseCase', () => {
	it('rejects when id is empty', async () => {
		const useCase = new DeleteStaffUseCase(makeRepo());
		await expect(useCase.execute('')).rejects.toThrow('ID staf wajib diisi.');
	});

	it('delegates to repository.deleteStaff with a valid id', async () => {
		const deleteStaffFn = vi.fn().mockResolvedValue(undefined);
		const useCase = new DeleteStaffUseCase(makeRepo(deleteStaffFn));

		await useCase.execute('staff_1');

		expect(deleteStaffFn).toHaveBeenCalledWith('staff_1');
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Staff not found');
		const useCase = new DeleteStaffUseCase(makeRepo(vi.fn().mockRejectedValue(repoError)));

		await expect(useCase.execute('staff_1')).rejects.toThrow('Staff not found');
	});
});
