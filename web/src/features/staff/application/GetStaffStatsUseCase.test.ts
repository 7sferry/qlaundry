import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import type {StaffStats} from '../domain/Staff';
import {GetStaffStatsUseCase} from './GetStaffStatsUseCase';

const mockStats: StaffStats = {totalStaff: 9, newStaff: 2};

function makeRepo(getStaffStatsImpl = vi.fn().mockResolvedValue(mockStats)): StaffRepository {
	return {
		getStaffList: vi.fn(),
		getStaffById: vi.fn(),
		createStaff: vi.fn(),
		updateStaff: vi.fn(),
		deleteStaff: vi.fn(),
		getStaffStats: getStaffStatsImpl,
	} as unknown as StaffRepository;
}

describe('GetStaffStatsUseCase', () => {
	it('delegates to repository.getStaffStats', async () => {
		const getStaffStatsFn = vi.fn().mockResolvedValue(mockStats);
		const useCase = new GetStaffStatsUseCase(makeRepo(getStaffStatsFn));

		const result = await useCase.execute();

		expect(getStaffStatsFn).toHaveBeenCalledOnce();
		expect(result).toBe(mockStats);
	});

	it('propagates errors thrown by the repository', async () => {
		const useCase = new GetStaffStatsUseCase(makeRepo(vi.fn().mockRejectedValue(new Error('Analytics down'))));

		await expect(useCase.execute()).rejects.toThrow('Analytics down');
	});
});
