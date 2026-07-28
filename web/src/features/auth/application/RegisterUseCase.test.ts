import {describe, expect, it, vi} from 'vitest';
import type {AuthRepository} from '../domain/AuthRepository';
import type {AuthSession, RegisterData, User} from '../domain/User';
import {RegisterUseCase} from './RegisterUseCase';

const mockUser: User = {
	id: '2',
	fullName: 'New User',
	username: 'newuser',
	email: 'new@test.com',
	role: 'staff',
};

const mockSession: AuthSession = {
	user: mockUser,
	tokens: {accessToken: 'tok_new123'},
};

const validData: RegisterData = {
	fullName: 'New User',
	username: 'newuser',
	email: 'new@test.com',
	password: 'secret123',
	captchaToken: 'turnstile-token',
};

function makeRepo(registerImpl = vi.fn().mockResolvedValue(mockSession)): AuthRepository {
	return {
		login: vi.fn(),
		register: registerImpl,
		logout: vi.fn().mockResolvedValue(undefined),
		getProfile: vi.fn().mockResolvedValue(mockUser),
	} as unknown as AuthRepository;
}

describe('RegisterUseCase', () => {
	it('rejects when fullName is empty', async () => {
		const useCase = new RegisterUseCase(makeRepo());
		await expect(useCase.execute({...validData, fullName: ''}))
				.rejects.toThrow('Semua field wajib diisi.');
	});

	it('rejects when username is empty', async () => {
		const useCase = new RegisterUseCase(makeRepo());
		await expect(useCase.execute({...validData, username: ''}))
				.rejects.toThrow('Semua field wajib diisi.');
	});

	it('rejects when email is empty', async () => {
		const useCase = new RegisterUseCase(makeRepo());
		await expect(useCase.execute({...validData, email: ''}))
				.rejects.toThrow('Semua field wajib diisi.');
	});

	it('rejects when password is empty', async () => {
		const useCase = new RegisterUseCase(makeRepo());
		await expect(useCase.execute({...validData, password: ''}))
				.rejects.toThrow('Semua field wajib diisi.');
	});

	it('rejects when password is shorter than 6 characters', async () => {
		const useCase = new RegisterUseCase(makeRepo());
		await expect(useCase.execute({...validData, password: 'abc'}))
				.rejects.toThrow('Password minimal 6 karakter.');
	});

	it('rejects when captchaToken is missing', async () => {
		const useCase = new RegisterUseCase(makeRepo());
		await expect(useCase.execute({...validData, captchaToken: ''}))
				.rejects.toThrow('Verifikasi captcha wajib diselesaikan.');
	});

	it('delegates to the repository with valid data', async () => {
		const registerFn = vi.fn().mockResolvedValue(mockSession);
		const useCase = new RegisterUseCase(makeRepo(registerFn));

		const result = await useCase.execute(validData);

		expect(registerFn).toHaveBeenCalledOnce();
		expect(registerFn).toHaveBeenCalledWith(validData);
		expect(result).toBe(mockSession);
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Username already taken');
		const failingRepo = makeRepo(vi.fn().mockRejectedValue(repoError));
		const useCase = new RegisterUseCase(failingRepo);

		await expect(useCase.execute(validData)).rejects.toThrow('Username already taken');
	});
});
