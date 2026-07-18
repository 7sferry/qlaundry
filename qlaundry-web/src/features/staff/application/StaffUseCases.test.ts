import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../domain/Staff';
import {staffUseCases} from './StaffUseCases';

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

function makeRepo(): { repo: StaffRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getStaffList: vi.fn().mockResolvedValue([mockStaff]),
		getStaffById: vi.fn().mockResolvedValue(mockStaff),
		createStaff: vi.fn().mockResolvedValue(mockStaff),
		updateStaff: vi.fn().mockResolvedValue(mockStaff),
		deleteStaff: vi.fn().mockResolvedValue(undefined),
	};
	return {repo: fns as unknown as StaffRepository, fns};
}

describe('staffUseCases', () => {
	it('listStaff delegates to repository.getStaffList', async () => {
		const {repo, fns} = makeRepo();
		const useCases = staffUseCases(repo);

		const result = await useCases.listStaff();

		expect(fns.getStaffList).toHaveBeenCalledOnce();
		expect(result).toEqual([mockStaff]);
	});

	it('listStaff passes filters through to the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = staffUseCases(repo);
		const filters = {search: 'Ratna'};

		await useCases.listStaff(filters);

		expect(fns.getStaffList).toHaveBeenCalledWith(filters);
	});

	it('getStaff delegates to repository.getStaffById', async () => {
		const {repo, fns} = makeRepo();
		const useCases = staffUseCases(repo);

		const result = await useCases.getStaff('staff_1');

		expect(fns.getStaffById).toHaveBeenCalledWith('staff_1');
		expect(result).toBe(mockStaff);
	});

	it('createStaff delegates to repository.createStaff with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = staffUseCases(repo);
		const input: CreateStaffInput = {
			username: 'budi.s',
			password: 'rahasia123',
			fullName: 'Budi Setiawan',
			emails: [],
			phones: ['08111222333'],
			addresses: [],
		};

		const result = await useCases.createStaff(input);

		expect(fns.createStaff).toHaveBeenCalledWith(input);
		expect(result).toBe(mockStaff);
	});

	it('updateStaff delegates to repository.updateStaff with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = staffUseCases(repo);
		const input: UpdateStaffInput = {id: 'staff_1', fullName: 'Ratna D.'};

		await useCases.updateStaff(input);

		expect(fns.updateStaff).toHaveBeenCalledWith(input);
	});

	it('deleteStaff delegates to repository.deleteStaff', async () => {
		const {repo, fns} = makeRepo();
		const useCases = staffUseCases(repo);

		await useCases.deleteStaff('staff_1');

		expect(fns.deleteStaff).toHaveBeenCalledWith('staff_1');
	});
});
