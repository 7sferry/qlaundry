/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {useEffect, useRef, useState} from 'react';
import {useNavigate, useSearchParams} from 'react-router-dom';
import {AlertTriangle, WashingMachine} from 'lucide-react';
import {Button, Loading} from '@/core/ui';
import {useAuth} from '../useAuth';
import {authRepository} from '../../infrastructure/AuthRepositoryImpl';
import {ConfirmTenantRegistrationUseCase} from '../../application/ConfirmTenantRegistrationUseCase';
import {ResendTenantConfirmationUseCase} from '../../application/ResendTenantConfirmationUseCase';

const confirmTenantRegistrationUseCase = new ConfirmTenantRegistrationUseCase(authRepository);
const resendTenantConfirmationUseCase = new ResendTenantConfirmationUseCase(authRepository);

type Status = 'confirming' | 'failed';
type ResendStatus = 'idle' | 'sending' | 'sent' | 'failed';

export default function ConfirmRegistrationPage() {
	const navigate = useNavigate();
	const [searchParams] = useSearchParams();
	const tenantId = searchParams.get('tenantId') ?? '';
	const token = searchParams.get('token') ?? '';
	const {isAuthenticated, isLoading, logout} = useAuth();

	const [status, setStatus] = useState<Status>('confirming');
	const [error, setError] = useState('This confirmation link is invalid or has expired.');
	const [resendStatus, setResendStatus] = useState<ResendStatus>('idle');
	const [resendMessage, setResendMessage] = useState('');
	const startedRef = useRef(false);

	useEffect(() => {
		// Wait for the initial session check to settle so we know whether an
		// account is currently logged in before deciding to log it out.
		if (isLoading || startedRef.current) return;
		startedRef.current = true;

		const run = async () => {
			if (isAuthenticated) {
				// A different (or the same) account may already be signed in on this
				// browser — sign it out first so confirming doesn't leave a stale session.
				await logout().catch(() => undefined);
			}
			return confirmTenantRegistrationUseCase.execute(tenantId, token);
		};

		run()
				.then(() => {
					navigate('/login', {
						state: {notice: 'Your registration has been confirmed. You can now sign in.'},
					});
				})
				.catch((err) => {
					setError(err instanceof Error ? err.message : 'This confirmation link is invalid or has expired.');
					setStatus('failed');
				});
	}, [isLoading, isAuthenticated, logout, navigate, tenantId, token]);

	const resend = () => {
		setResendStatus('sending');
		setResendMessage('');
		resendTenantConfirmationUseCase.execute(tenantId)
				.then((message) => {
					setResendStatus('sent');
					setResendMessage(message);
				})
				.catch((err) => {
					setResendStatus('failed');
					setResendMessage(err instanceof Error ? err.message : 'Failed to resend the confirmation email.');
				});
	};

	return (
			<div className="auth-shell auth-shell--wide">
				<div className="auth-card" style={{maxWidth: 440, margin: '0 auto'}}>
					<div className="auth-card__brand">
						<span className="sidebar__logo"><WashingMachine size={20}/></span>
						QLaundry
					</div>

					{status === 'confirming' && (
							<div style={{padding: '24px 0'}}>
								<Loading label="Confirming your registration…"/>
							</div>
					)}

					{status === 'failed' && (
							<>
								<p className="eyebrow">Confirmation failed</p>
								<h2 style={{paddingBottom: '.5em'}}>We couldn't confirm your registration</h2>

								{resendStatus === 'idle' && (
										<div className="alert alert--error" style={{display: 'flex', alignItems: 'flex-start', gap: 8}}>
											<AlertTriangle size={16}/>
											<span>{error}</span>
										</div>
								)}

								{tenantId && (
										<>
											{resendStatus === 'sent' ? (
													<div className="alert alert--info">{resendMessage}</div>
											) : (
													<>
														<p className="muted auth-intro">
															The link may have expired. Request a new confirmation email below.
														</p>
														{resendStatus === 'failed' && (
																<div className="alert alert--error">{resendMessage}</div>
														)}
														<Button block type="button" disabled={resendStatus === 'sending'} onClick={resend}>
															{resendStatus === 'sending' ? 'Sending…' : 'Resend confirmation email'}
														</Button>
													</>
											)}
										</>
								)}

								<p className="auth-switch">
									<button type="button" onClick={() => navigate('/login')}>
										Back to sign-in
									</button>
								</p>
							</>
					)}
				</div>
			</div>
	);
}
