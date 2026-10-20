/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useRef, useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {ArrowRight, KeyRound, LockKeyhole, User2, WashingMachine} from 'lucide-react';
import {Button, Field, Input, PasswordInput, Turnstile, type TurnstileHandle} from '@/core/ui';
import {env} from '@/core/config/env';
import {USERNAME_MAX_LENGTH} from '@/core/utils/username';
import {ForgotPasswordUseCase} from '../../application/ForgotPasswordUseCase';
import {LoginChallengeError, LoginValidationError} from '../../domain/errors';
import type {LoginChallenge} from '../../domain/User';
import {authRepository} from '../../infrastructure/AuthRepositoryImpl';
import {useAuth} from '../useAuth';

const requestCodeUseCase = new ForgotPasswordUseCase(authRepository);

export default function LoginPage() {
	const {login} = useAuth();
	const navigate = useNavigate();
	const location = useLocation();
	const notice = (location.state as {notice?: string} | null)?.notice;
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const [otp, setOtp] = useState('');
	const [challenge, setChallenge] = useState<LoginChallenge | null>(null);
	const [captchaToken, setCaptchaToken] = useState('');
	const [codeNotice, setCodeNotice] = useState('');
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(false);
	const [sendingCode, setSendingCode] = useState(false);
	const turnstileRef = useRef<TurnstileHandle>(null);

	const resetCaptcha = () => {
		turnstileRef.current?.reset();
		setCaptchaToken('');
	};

	const changeUsername = (value: string) => {
		setUsername(value);
		if (challenge) {
			setChallenge(null);
			setCodeNotice('');
			setOtp('');
			setCaptchaToken('');
		}
	};

	const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError('');
		if (challenge === 'CAPTCHA' && !captchaToken) {
			setError('Complete the captcha verification first.');
			return;
		}
		setLoading(true);
		try {
			await login(challenge === 'OTP'
					? {username, password: '', otp}
					: {username, password, captchaToken: challenge === 'CAPTCHA' ? captchaToken : undefined});
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Sign-in failed.');
			if (err instanceof LoginChallengeError) {
				setChallenge(err.challenge);
			} else if (!(err instanceof LoginValidationError)) {
				setChallenge(null);
			}
			if (challenge === 'CAPTCHA') resetCaptcha();
		} finally {
			setLoading(false);
		}
	};

	const sendCode = async () => {
		setError('');
		setCodeNotice('');
		setSendingCode(true);
		try {
			const maskedEmail = await requestCodeUseCase.execute(username);
			setCodeNotice(`A verification code was sent to ${maskedEmail}. It is valid for a few minutes.`);
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Failed to send the verification code.');
		} finally {
			setSendingCode(false);
		}
	};

	return (
			<div className="auth-shell">
				<div className="auth-aside">
					<div className="sidebar__logo">
						<WashingMachine size={26}/>
					</div>
					<h1>Run your laundry smarter.</h1>
					<p>Track orders, pickups, and business reports — all in one place.</p>
					<div className="auth-features">
						<div className="auth-feature"><span className="auth-feature__dot"/>Real-time order tracking</div>
						<div className="auth-feature"><span className="auth-feature__dot"/>Customer management</div>
						<div className="auth-feature"><span className="auth-feature__dot"/>Reports & analytics</div>
					</div>
					<div className="auth-quote">
						"A tidy laundry business starts with organised management."
						<small>— QLaundry Team</small>
					</div>
				</div>

				<form className="auth-card" onSubmit={submit}>
					<div className="auth-card__brand">
						<span className="sidebar__logo"><WashingMachine size={20}/></span>
						QLaundry
					</div>
					<p className="eyebrow">Welcome back</p>
					<h2>Sign in to the dashboard</h2>
					<p className="muted auth-intro">Manage all your laundry operations from here.</p>

					{notice && !error && !codeNotice && <div className="alert alert--info">{notice}</div>}
					{codeNotice && !error && <div className="alert alert--info">{codeNotice}</div>}
					{error && <div className="alert alert--error">{error}</div>}

					<Field label="Username" htmlFor="username">
						<div className="input-with-icon">
							<User2 size={16}/>
							<Input
									id="username"
									value={username}
									maxLength={USERNAME_MAX_LENGTH}
									onChange={(e) => changeUsername(e.target.value)}
									autoComplete="off"
 								placeholder="Enter username"
							/>
						</div>
					</Field>

					{challenge !== 'OTP' && (
							<Field label="Password" htmlFor="password">
								<div className="input-with-icon">
									<LockKeyhole size={16}/>
									<PasswordInput
											id="password"
											value={password}
											onChange={(e) => setPassword(e.target.value)}
											autoComplete="current-password"
 										placeholder="Enter password"
									/>
								</div>
							</Field>
					)}

					{challenge === 'CAPTCHA' && (
							<Field>
								<Turnstile
										ref={turnstileRef}
										siteKey={env.turnstileSiteKey}
										onVerify={setCaptchaToken}
										onExpire={() => setCaptchaToken('')}
								/>
							</Field>
					)}

					{challenge === 'OTP' && (
							<>
								<Field label="Verification code" htmlFor="otp">
									<div className="input-with-icon">
										<KeyRound size={16}/>
										<Input
												id="otp"
												value={otp}
												onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
												autoComplete="one-time-code"
												inputMode="numeric"
												placeholder="000000"
												style={{letterSpacing: 6, fontWeight: 600}}
										/>
									</div>
								</Field>
								<Button block type="button" variant="subtle" disabled={sendingCode || !username.trim()}
								        onClick={sendCode} style={{marginBottom: 8}}>
									{sendingCode ? 'Sending…' : 'Send code to my email'}
								</Button>
							</>
					)}

					<Button block type="submit" disabled={loading}>
						{loading ? 'Processing…' : <><span>Sign in</span> <ArrowRight size={16}/></>}
					</Button>

					<p className="auth-switch">
						Forgot your password?{' '}
						<button type="button" onClick={() => navigate('/forgot-password')}>
							Reset it here
						</button>
					</p>

					<p className="auth-switch">
						Don’t have an account?{' '}
						<button type="button" onClick={() => navigate('/register')}>
							Register now
						</button>
					</p>

				</form>
			</div>
	);
}
