/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {Card, Loading, PageHeader, useToast} from '@/core/ui';
import {useAuth} from '@/features/auth/presentation/useAuth';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import {GetStaffUseCase} from '../../application/GetStaffUseCase';
import {UpdateStaffUseCase} from '../../application/UpdateStaffUseCase';
import {staffRepository} from '../../infrastructure/StaffRepositoryImpl';
import StaffForm from '../components/StaffForm';
import {type StaffFormData, emptyStaffForm} from '../components/staffFormData';
import type {UpdateStaffInput} from '../../domain/Staff';

const getStaffUseCase = new GetStaffUseCase(staffRepository);
const updateStaffUseCase = new UpdateStaffUseCase(staffRepository);

export default function StaffSettingsPage() {
	const {user} = useAuth();
	const toast = useToast();

	const [form, setForm] = useState<StaffFormData>(emptyStaffForm);
	const [original, setOriginal] = useState<StaffFormData>(emptyStaffForm);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [saving, setSaving] = useState(false);

	useOnceEffect(() => {
		if (!user) return;
		getStaffUseCase.execute(user.id)
				.then((s) => {
					const data: StaffFormData = {
						username: s.username,
						password: '',
						fullName: s.fullName,
						description: s.description ?? '',
						role: 'STAFF',
						email: s.emails[0] ?? '',
						phone: s.phones[0] ?? '',
						address: s.addresses[0] ?? '',
					};
					setForm(data);
					setOriginal(data);
				})
				.catch((err) => setError(err instanceof Error ? err.message : 'Gagal memuat profil'))
				.finally(() => setLoading(false));
	});

	const update = (key: keyof StaffFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		if (!user) return;
		setSaving(true);
		try {
			const input: UpdateStaffInput = {
				id: user.id,
				fullName: form.fullName,
				description: form.description || undefined,
				emails: form.email ? [form.email] : [],
				phones: form.phone ? [form.phone] : [],
				addresses: form.address ? [form.address] : [],
			};
			await updateStaffUseCase.execute(input);
			setOriginal(form);
			toast.success('Profil berhasil diperbarui.');
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Terjadi kesalahan. Coba lagi.');
		} finally {
			setSaving(false);
		}
	};

	if (loading) return <Loading label="Memuat profil…"/>;

	return (
			<>
				<PageHeader title="Pengaturan akun" description="Perbarui data profil Anda sendiri"/>

				<Card style={{marginTop: 24, maxWidth: 520, marginLeft: 'auto', marginRight: 'auto'}}>
					{error ? (
							<p className="muted">{error}</p>
					) : (
							<StaffForm
									form={form}
									update={update}
									onSubmit={handleSave}
									onCancel={() => setForm(original)}
									saving={saving}
									editMode
							/>
					)}
				</Card>
			</>
	);
}
