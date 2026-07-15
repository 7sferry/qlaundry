import {beforeEach, describe, expect, it, vi} from 'vitest';
import type {AuthRepository} from '../domain/AuthRepository';
import type {AuthSession, User} from '../domain/User';
import {LoginUseCase} from './LoginUseCase';

const mockUser: User = {
	id: '1',
	fullName: 'Admin User',
	username: 'admin',
	email: 'admin@test.com',
	role: 'admin',
};

const mockSession: AuthSession = {
	user: mockUser,
	tokens: {accessToken: 'tok_abc123'},
};

function makeRepo(loginImpl: ReturnType<typeof vi.fn>): AuthRepository {
	return {
		login: loginImpl,
		register: vi.fn(),
		logout: vi.fn().mockResolvedValue(undefined),
		getProfile: vi.fn().mockResolvedValue(mockUser),
	} as unknown as AuthRepository;
}

describe('LoginUseCase', () => {
	let loginFn: ReturnType<typeof vi.fn>;
	let useCase: LoginUseCase;

	beforeEach(() => {
		loginFn = vi.fn().mockResolvedValue(mockSession);
		useCase = new LoginUseCase(makeRepo(loginFn));
	});

	it('rejects when username is empty', async () => {
		await expect(useCase.execute({username: '', password: 'secret'}))
				.rejects.toThrow('Masukkan username dan password.');
	});

	it('rejects when password is empty', async () => {
		await expect(useCase.execute({username: 'admin', password: ''}))
				.rejects.toThrow('Masukkan username dan password.');
	});

	it('rejects when both fields are whitespace only', async () => {
		await expect(useCase.execute({username: '   ', password: '   '}))
				.rejects.toThrow('Masukkan username dan password.');
	});

	it('delegates to the repository with valid credentials', async () => {
		const credentials = {username: 'admin', password: 'admin123'};
		const result = await useCase.execute(credentials);

		expect(loginFn).toHaveBeenCalledOnce();
		expect(loginFn).toHaveBeenCalledWith(credentials);
		expect(result).toBe(mockSession);
	});

	it('propagates errors thrown by the repository', async () => {
		const repoError = new Error('Invalid credentials');
		const failingUseCase = new LoginUseCase(makeRepo(vi.fn().mockRejectedValue(repoError)));

		await expect(failingUseCase.execute({username: 'admin', password: 'wrong'}))
				.rejects.toThrow('Invalid credentials');
	});
});
