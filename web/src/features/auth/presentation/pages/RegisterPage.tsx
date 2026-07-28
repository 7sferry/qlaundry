/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {ArrowLeft, ArrowRight, Building2, LockKeyhole, Mail, MapPin, Phone, User2, WashingMachine} from 'lucide-react';
import {Button, Field, Input, Turnstile, type TurnstileHandle} from '@/core/ui';
import {env} from '@/core/config/env';
import {useAuth} from '../useAuth';

const initialForm = {
	fullName: '',
	username: '',
	email: '',
	phone: '',
	password: '',
	confirmPassword: '',
	outletName: '',
	address: '',
};

type RegisterForm = typeof initialForm;
type FieldKey = keyof RegisterForm;

const REQUIRED_FIELDS: FieldKey[] = ['fullName', 'outletName', 'email', 'username', 'password', 'confirmPassword'];
const REQUIRED_MESSAGE = 'Wajib diisi.';

function validateForm(form: RegisterForm): Partial<Record<FieldKey, string>> {
	const errors: Partial<Record<FieldKey, string>> = {};
	for (const key of REQUIRED_FIELDS) {
		if (!form[key].trim()) errors[key] = REQUIRED_MESSAGE;
	}
	if (!errors.password && form.password.length < 6) {
		errors.password = 'Password minimal 6 karakter.';
	}
	if (!errors.confirmPassword && form.confirmPassword !== form.password) {
		errors.confirmPassword = 'Konfirmasi password tidak cocok.';
	}
	return errors;
}

export default function RegisterPage() {
	const {register} = useAuth();
	const navigate = useNavigate();
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(false);
	const [form, setForm] = useState<RegisterForm>(initialForm);
	const [fieldErrors, setFieldErrors] = useState<Partial<Record<FieldKey, string>>>({});
	const [captchaToken, setCaptchaToken] = useState('');
	const [captchaError, setCaptchaError] = useState('');
	const turnstileRef = useRef<TurnstileHandle>(null);

	const update = (key: FieldKey) => (e: React.ChangeEvent<HTMLInputElement>) => {
		const nextForm = {...form, [key]: e.target.value};
		setForm(nextForm);
		// Re-validate live so a message clears the moment the user fixes it,
		// instead of only on the next submit attempt.
		setFieldErrors((prevErrors) => {
			if (Object.keys(prevErrors).length === 0) return prevErrors;
			const errors = validateForm(nextForm);
			const affected: FieldKey[] = key === 'password' || key === 'confirmPassword'
					? ['password', 'confirmPassword']
					: [key];
			const merged = {...prevErrors};
			for (const affectedKey of affected) {
				if (errors[affectedKey]) merged[affectedKey] = errors[affectedKey];
				else delete merged[affectedKey];
			}
			return merged;
		});
	};

	const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setError('');
		const errors = validateForm(form);
		if (Object.keys(errors).length > 0) {
			setFieldErrors(errors);
			return;
		}
		setFieldErrors({});
		if (!captchaToken) {
			setCaptchaError('Verifikasi captcha wajib diselesaikan.');
			return;
		}
		setCaptchaError('');
		setLoading(true);
		try {
			await register({
				fullName: form.fullName,
				username: form.username,
				email: form.email,
				phone: form.phone,
				password: form.password,
				outletName: form.outletName,
				address: form.address,
				captchaToken,
			});
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Pendaftaran gagal.');
			// Turnstile tokens are single-use — force a fresh challenge on retry.
			turnstileRef.current?.reset();
			setCaptchaToken('');
		} finally {
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

					<form onSubmit={submit} className="register-grid" noValidate>
						<div className="register-col">
							<Field label="Nama lengkap" htmlFor="fullName" error={fieldErrors.fullName}>
								<div className="input-with-icon">
									<User2 size={16}/>
									<Input
											id="fullName"
											required
											autoComplete="off"
											value={form.fullName}
											onChange={update('fullName')}
											placeholder="Ahmad Budi"
									/>
								</div>
							</Field>
							<Field label="Nama outlet / toko" htmlFor="outletName" error={fieldErrors.outletName}>
								<div className="input-with-icon">
									<Building2 size={16}/>
									<Input
											id="outletName"
											required
											autoComplete="off"
											value={form.outletName}
											onChange={update('outletName')}
											placeholder="Laundry Bersih Jaya"
									/>
								</div>
							</Field>
							<Field label="Alamat" htmlFor="address" error={fieldErrors.address}>
								<div className="input-with-icon">
									<MapPin size={16}/>
									<Input
											id="address"
											autoComplete="off"
											value={form.address}
											onChange={update('address')}
											placeholder="Jl. Merdeka No. 10, Jakarta"
									/>
								</div>
							</Field>
							<Field label="Email" htmlFor="email" error={fieldErrors.email}>
								<div className="input-with-icon">
									<Mail size={16}/>
									<Input
											id="email"
											type="email"
											required
											autoComplete="off"
											value={form.email}
											onChange={update('email')}
											placeholder="email@contoh.com"
									/>
								</div>
							</Field>
							<Field label="Nomor telepon" htmlFor="phone" error={fieldErrors.phone}>
								<div className="input-with-icon">
									<Phone size={16}/>
									<Input
											id="phone"
											type="tel"
											autoComplete="off"
											value={form.phone}
											onChange={update('phone')}
											placeholder="08xxxxxxxxxx"
									/>
								</div>
							</Field>
						</div>

						<div className="register-col">
							<Field label="Username" htmlFor="username" error={fieldErrors.username}>
								<div className="input-with-icon">
									<User2 size={16}/>
									<Input
											id="username"
											required
											autoComplete="off"
											value={form.username}
											onChange={update('username')}
											placeholder="Minimal 4 karakter"
									/>
								</div>
							</Field>
							<Field label="Password" htmlFor="password" error={fieldErrors.password}>
								<div className="input-with-icon">
									<LockKeyhole size={16}/>
									<Input
											id="password"
											type="password"
											required
											minLength={6}
											autoComplete="new-password"
											value={form.password}
											onChange={update('password')}
											placeholder="Minimal 6 karakter"
									/>
								</div>
							</Field>
							<Field label="Konfirmasi password" htmlFor="confirmPassword" error={fieldErrors.confirmPassword}>
								<div className="input-with-icon">
									<LockKeyhole size={16}/>
									<Input
											id="confirmPassword"
											type="password"
											required
											autoComplete="new-password"
											value={form.confirmPassword}
											onChange={update('confirmPassword')}
											placeholder="Ulangi password"
									/>
								</div>
							</Field>

							<Field error={captchaError}>
								<Turnstile
										ref={turnstileRef}
										siteKey={env.turnstileSiteKey}
										onVerify={(token) => {
											setCaptchaToken(token);
											setCaptchaError('');
										}}
										onExpire={() => setCaptchaToken('')}
								/>
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
