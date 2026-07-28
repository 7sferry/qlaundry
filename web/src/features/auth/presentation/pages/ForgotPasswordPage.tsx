/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {ArrowRight, CheckCircle2, KeyRound, LockKeyhole, User2, WashingMachine} from 'lucide-react';
import {Button, Field, Input} from '@/core/ui';
import {useToast} from '@/core/ui/useToast';
import {ResetSessionExpiredError} from '../../domain/errors';
import {authRepository} from '../../infrastructure/AuthRepositoryImpl';
import {ForgotPasswordUseCase} from '../../application/ForgotPasswordUseCase';
import {SubmitOtpUseCase} from '../../application/SubmitOtpUseCase';
import {ResetPasswordUseCase} from '../../application/ResetPasswordUseCase';

const forgotPasswordUseCase = new ForgotPasswordUseCase(authRepository);
const submitOtpUseCase = new SubmitOtpUseCase(authRepository);
const resetPasswordUseCase = new ResetPasswordUseCase(authRepository);

type Step = 'username' | 'otp' | 'password' | 'done';

export default function ForgotPasswordPage() {
	const navigate = useNavigate();
	const toast = useToast();
	const [step, setStep] = useState<Step>('username');
	const [username, setUsername] = useState('');
	const [maskedEmail, setMaskedEmail] = useState('');
	const [otp, setOtp] = useState('');
	const [resetToken, setResetToken] = useState('');
	const [password, setPassword] = useState('');
	const [confirmPassword, setConfirmPassword] = useState('');
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(false);

	const run = async (action: () => Promise<void>) => {
		setError('');
		setLoading(true);
		try {
			await action();
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
		} finally {
			setLoading(false);
		}
	};

	const submitUsername = (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		void run(async () => {
			setMaskedEmail(await forgotPasswordUseCase.execute(username));
			setStep('otp');
		});
	};

	const submitOtp = (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		void run(async () => {
			setResetToken(await submitOtpUseCase.execute(username, otp));
			setStep('password');
		});
	};

	const submitPassword = (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		void run(async () => {
			try {
				await resetPasswordUseCase.execute(username, password, confirmPassword, resetToken);
			} catch (err) {
				if (err instanceof ResetSessionExpiredError) {
					// The single-use token expired or was consumed — the whole
					// flow must be redone, so send the user back to login.
					toast.error(err.message);
					navigate('/login');
					return;
				}
				throw err;
			}
			setStep('done');
		});
	};

	const submitHandler = {username: submitUsername, otp: submitOtp, password: submitPassword, done: undefined}[step];

	return (
			<div className="auth-shell auth-shell--wide">
				<form className="auth-card" style={{maxWidth: 440, margin: '0 auto'}} onSubmit={submitHandler}>
					<div className="auth-card__brand">
						<span className="sidebar__logo"><WashingMachine size={20}/></span>
						QLaundry
					</div>
					<p className="eyebrow">Forgot password</p>
					<h2>Reset your password</h2>

					{error && step !== 'done' && <div className="alert alert--error">{error}</div>}

					{step === 'username' && (
							<>
  						<p className="muted auth-intro">
  							Enter your username. We’ll send a verification code to your registered email address.
  						</p>

								<Field label="Username" htmlFor="username">
									<div className="input-with-icon">
										<User2 size={16}/>
										<Input
												id="username"
												value={username}
												onChange={(e) => setUsername(e.target.value)}
												autoComplete="off"
    								placeholder="Enter username"
										/>
									</div>
								</Field>

								<Button block type="submit" disabled={loading}>
  							{loading ? 'Sending…' : <><span>Send verification code</span> <ArrowRight size={16}/></>}
								</Button>
							</>
					)}

					{step === 'otp' && (
							<>
  						<p className="muted auth-intro">
  							We’ve sent a verification code to <strong>{maskedEmail}</strong>. Enter the 6-digit code within 3 minutes.
  						</p>

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

								<Button block type="submit" disabled={loading}>
  							{loading ? 'Verifying…' : <><span>Verify code</span> <ArrowRight size={16}/></>}
								</Button>
							</>
					)}

					{step === 'password' && (
							<>
  						<p className="muted auth-intro">
  							Code verified. Create a new password for <strong>{username}</strong>.
  						</p>

  						<Field label="New password" htmlFor="new-password">
									<div className="input-with-icon">
										<LockKeyhole size={16}/>
										<Input
												id="new-password"
												type="password"
												value={password}
												onChange={(e) => setPassword(e.target.value)}
												autoComplete="new-password"
    								placeholder="At least 6 characters"
										/>
									</div>
								</Field>

  						<Field label="Confirm new password" htmlFor="confirm-password">
									<div className="input-with-icon">
										<LockKeyhole size={16}/>
										<Input
												id="confirm-password"
												type="password"
												value={confirmPassword}
												onChange={(e) => setConfirmPassword(e.target.value)}
												autoComplete="new-password"
    								placeholder="Repeat new password"
										/>
									</div>
								</Field>

								<Button block type="submit" disabled={loading}>
  							{loading ? 'Saving…' : <><span>Save new password</span> <ArrowRight size={16}/></>}
								</Button>
							</>
					)}

					{step === 'done' && (
							<>
								<div className="alert alert--info">
									<CheckCircle2 size={16}/>{' '}
  							Your password has been changed. Please sign in with the new password.
								</div>
								<Button block type="button" onClick={() => navigate('/login')}>
  							Sign in now
								</Button>
							</>
					)}

					{step !== 'done' && (
							<p className="auth-switch">
  						Remember your password?{' '}
								<button type="button" onClick={() => navigate('/login')}>
  							Sign in
								</button>
							</p>
					)}
				</form>
			</div>
	);
}
