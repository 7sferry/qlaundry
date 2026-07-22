/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {ArrowRight, LockKeyhole, User2, WashingMachine} from 'lucide-react';
import {Button, Field, Input} from '@/core/ui';
import {useAuth} from '../useAuth';

export default function LoginPage() {
	const {login} = useAuth();
	const navigate = useNavigate();
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
			setError(err instanceof Error ? err.message : 'Login gagal.');
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
					<h1>Kelola laundry Anda dengan lebih cerdas.</h1>
					<p>Pantau pesanan, jadwal pengambilan, dan laporan bisnis — semua dalam satu tempat.</p>
					<div className="auth-features">
						<div className="auth-feature"><span className="auth-feature__dot"/>Pelacakan pesanan real-time</div>
						<div className="auth-feature"><span className="auth-feature__dot"/>Manajemen pelanggan</div>
						<div className="auth-feature"><span className="auth-feature__dot"/>Laporan & analitik bisnis</div>
					</div>
					<div className="auth-quote">
						"Bisnis laundry yang rapi dimulai dari pengelolaan yang terorganisir."
						<small>— Tim QLaundry</small>
					</div>
				</div>

				<form className="auth-card" onSubmit={submit}>
					<div className="auth-card__brand">
						<span className="sidebar__logo"><WashingMachine size={20}/></span>
						QLaundry
					</div>
					<p className="eyebrow">Selamat datang kembali</p>
					<h2>Masuk ke dashboard</h2>
					<p className="muted auth-intro">Kelola semua aktivitas laundry Anda dari sini.</p>

					{error && <div className="alert alert--error">{error}</div>}

					<Field label="Username" htmlFor="username">
						<div className="input-with-icon">
							<User2 size={16}/>
							<Input
									id="username"
									value={username}
									onChange={(e) => setUsername(e.target.value)}
									autoComplete="off"
									placeholder="Masukkan username"
							/>
						</div>
					</Field>

					<Field label="Password" htmlFor="password">
						<div className="input-with-icon">
							<LockKeyhole size={16}/>
							<Input
									id="password"
									type="password"
									value={password}
									onChange={(e) => setPassword(e.target.value)}
									autoComplete="current-password"
									placeholder="Masukkan password"
							/>
						</div>
					</Field>

					<Button block type="submit" disabled={loading}>
						{loading ? 'Memproses…' : <><span>Masuk</span> <ArrowRight size={16}/></>}
					</Button>

					<p className="auth-switch">
						Lupa password?{' '}
						<button type="button" onClick={() => navigate('/forgot-password')}>
							Atur ulang di sini
						</button>
					</p>

					<p className="auth-switch">
						Belum punya akun?{' '}
						<button type="button" onClick={() => navigate('/register')}>
							Daftar sekarang
						</button>
					</p>

				</form>
			</div>
	);
}
