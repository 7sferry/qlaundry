/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {ArrowRight, LockKeyhole, User2, WashingMachine} from 'lucide-react';
import {Button, Field, Input, PasswordInput} from '@/core/ui';
import {useAuth} from '../useAuth';

export default function LoginPage() {
	const {login} = useAuth();
	const navigate = useNavigate();
	const location = useLocation();
	const notice = (location.state as {notice?: string} | null)?.notice;
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(false);

	const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError('');
		setLoading(true);
		try {
			await login({username, password});
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Sign-in failed.');
		} finally {
			setLoading(false);
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

					{notice && !error && <div className="alert alert--info">{notice}</div>}
					{error && <div className="alert alert--error">{error}</div>}

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
