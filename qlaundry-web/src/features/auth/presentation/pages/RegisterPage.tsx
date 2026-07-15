/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {ArrowLeft, ArrowRight, Building2, LockKeyhole, Mail, Phone, User2, WashingMachine} from 'lucide-react';
import {Button, Field, Input} from '@/core/ui';
import {useAuth} from '../useAuth';

export default function RegisterPage() {
	const {register} = useAuth();
	const navigate = useNavigate();
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(false);
	const [form, setForm] = useState({
		fullName: '',
		username: '',
		email: '',
		phone: '',
		password: '',
		confirmPassword: '',
		outletName: '',
	});

	const update = (key: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
			setForm((prev) => ({...prev, [key]: e.target.value}));

	const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError('');
		if (form.password !== form.confirmPassword) {
			setError('Konfirmasi password tidak cocok.');
			return;
		}
		setLoading(true);
		try {
			await register({
				fullName: form.fullName,
				username: form.username,
				email: form.email,
				phone: form.phone,
				password: form.password,
				outletName: form.outletName,
			});
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Pendaftaran gagal.');
			setLoading(false);
		}
	};

	return (
			<div className="auth-shell auth-shell--wide">
				<div className="auth-card auth-card--wide">
					<div className="auth-card__brand">
						<span className="sidebar__logo"><WashingMachine size={20}/></span>
						QLaundry
					</div>
					<p className="eyebrow">Mulai sekarang</p>
					<h2>Buat akun baru</h2>
					<p className="muted auth-intro">Daftarkan outlet laundry Anda dan mulai mengelola bisnis lebih efisien.</p>

					{error && <div className="alert alert--error">{error}</div>}

					<form onSubmit={submit} className="register-grid">
						<div className="register-col">
							<Field label="Nama lengkap" htmlFor="fullName">
								<div className="input-with-icon">
									<User2 size={16}/>
									<Input
											id="fullName"
											required
											value={form.fullName}
											onChange={update('fullName')}
											placeholder="Ahmad Budi"
									/>
								</div>
							</Field>
							<Field label="Nama outlet / toko" htmlFor="outletName">
								<div className="input-with-icon">
									<Building2 size={16}/>
									<Input
											id="outletName"
											value={form.outletName}
											onChange={update('outletName')}
											placeholder="Laundry Bersih Jaya"
									/>
								</div>
							</Field>
							<Field label="Email" htmlFor="email">
								<div className="input-with-icon">
									<Mail size={16}/>
									<Input
											id="email"
											type="email"
											required
											value={form.email}
											onChange={update('email')}
											placeholder="email@contoh.com"
									/>
								</div>
							</Field>
							<Field label="Nomor telepon" htmlFor="phone">
								<div className="input-with-icon">
									<Phone size={16}/>
									<Input
											id="phone"
											type="tel"
											value={form.phone}
											onChange={update('phone')}
											placeholder="08xxxxxxxxxx"
									/>
								</div>
							</Field>
						</div>

						<div className="register-col">
							<Field label="Username" htmlFor="username">
								<div className="input-with-icon">
									<User2 size={16}/>
									<Input
											id="username"
											required
											value={form.username}
											onChange={update('username')}
											placeholder="Minimal 4 karakter"
									/>
								</div>
							</Field>
							<Field label="Password" htmlFor="password">
								<div className="input-with-icon">
									<LockKeyhole size={16}/>
									<Input
											id="password"
											type="password"
											required
											minLength={6}
											value={form.password}
											onChange={update('password')}
											placeholder="Minimal 6 karakter"
									/>
								</div>
							</Field>
							<Field label="Konfirmasi password" htmlFor="confirmPassword">
								<div className="input-with-icon">
									<LockKeyhole size={16}/>
									<Input
											id="confirmPassword"
											type="password"
											required
											value={form.confirmPassword}
											onChange={update('confirmPassword')}
											placeholder="Ulangi password"
									/>
								</div>
							</Field>

							<Button block type="submit" disabled={loading} style={{marginTop: 8}}>
								{loading ? 'Mendaftarkan…' : <><span>Buat akun</span> <ArrowRight size={16}/></>}
							</Button>
						</div>
					</form>

					<p className="auth-switch">
						<button type="button" onClick={() => navigate('/login')}>
							<ArrowLeft size={14}/> Kembali ke halaman masuk
						</button>
					</p>
				</div>
			</div>
	);
}
