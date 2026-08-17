import {describe, expect, it, vi} from 'vitest';
import type {StaffRepository} from '../domain/StaffRepository';
import type {CreateStaffInput, Staff} from '../domain/Staff';
import {CreateStaffUseCase} from './CreateStaffUseCase';

const mockStaff: Staff = {
	id: 'staff_2',
	username: 'budi.s',
	fullName: 'Budi Setiawan',
	emails: ['budi@qlaundry.id'],
	phones: ['+628111222333'],
	addresses: [],
	joinedAt: '2025-03-01',
};

const validInput: CreateStaffInput = {
	username: 'budi.s',
	password: 'rahasia123',
	fullName: 'Budi Setiawan',
	role: 'STAFF',
	emails: ['budi@qlaundry.id'],
	phones: ['+628111222333'],
	addresses: [],
};

function makeRepo(createStaffImpl = vi.fn().mockResolvedValue(mockStaff)): StaffRepository {
	return {
		getStaffList: vi.fn(),
		getStaffById: vi.fn(),
		createStaff: createStaffImpl,
		updateStaff: vi.fn(),
		deleteStaff: vi.fn(),
	} as unknown as StaffRepository;
}

describe('CreateStaffUseCase', () => {
	it('rejects when username is empty', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, username: ''}))
				.rejects.toThrow('Username, password, nama lengkap, dan email wajib diisi.');
	});

	it('rejects when password is empty', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, password: ''}))
				.rejects.toThrow('Username, password, nama lengkap, dan email wajib diisi.');
	});

	it('rejects when fullName is empty', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, fullName: ''}))
				.rejects.toThrow('Username, password, nama lengkap, dan email wajib diisi.');
	});

	it('rejects when email is missing', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, emails: []}))
				.rejects.toThrow('Username, password, nama lengkap, dan email wajib diisi.');
	});

	it('rejects when username is shorter than 5 characters', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, username: 'bud'}))
				.rejects.toThrow('Username minimal 5 karakter.');
	});

	it('rejects when password is shorter than 8 characters', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, password: 'abc123'}))
				.rejects.toThrow('Password minimal 8 karakter.');
	});

	it('rejects when email format is invalid', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, emails: ['not-an-email']}))
				.rejects.toThrow('Format email tidak valid.');
	});

	it('rejects when a phone is not in E.164 format', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, phones: ['+62 811-222-333']}))
				.rejects.toThrow('Nomor telepon harus dalam format E.164, contoh: +6281234567890.');
	});

	it('rejects when role is invalid', async () => {
		const useCase = new CreateStaffUseCase(makeRepo());
		await expect(useCase.execute({...validInput, role: 'OWNER' as CreateStaffInput['role']}))
				.rejects.toThrow('Role wajib dipilih.');
	});

	it('delegates to the repository with valid input', async () => {
		const createStaffFn = vi.fn().mockResolvedValue(mockStaff);
		const useCase = new CreateStaffUseCase(makeRepo(createStaffFn));

		const result = await useCase.execute(validInput);

		expect(createStaffFn).toHaveBeenCalledWith(validInput);
		expect(result).toBe(mockStaff);
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Username already taken');
		const useCase = new CreateStaffUseCase(makeRepo(vi.fn().mockRejectedValue(repoError)));

		await expect(useCase.execute(validInput)).rejects.toThrow('Username already taken');
	});
});
