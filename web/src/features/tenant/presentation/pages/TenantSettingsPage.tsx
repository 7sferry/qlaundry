/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

import React, {useState} from 'react';
import {Button, Card, Field, Input, Loading, PageHeader, Select, Textarea, useToast} from '@/core/ui';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import {GetTenantUseCase} from '../../application/GetTenantUseCase';
import {UpdateTenantUseCase} from '../../application/UpdateTenantUseCase';
import {tenantRepository} from '../../infrastructure/TenantRepositoryImpl';
import type {UpdateTenantInput} from '../../domain/Tenant';

const getTenantUseCase = new GetTenantUseCase(tenantRepository);
const updateTenantUseCase = new UpdateTenantUseCase(tenantRepository);

function resolveTimeZones(): string[] {
	try {
		return Intl.supportedValuesOf('timeZone');
	} catch {
		return ['UTC', 'Asia/Jakarta', 'Asia/Makassar', 'Asia/Jayapura'];
	}
}

const TIME_ZONES = resolveTimeZones();

interface TenantFormData {
	tenantName: string;
	description: string;
	timeZone: string;
}

const emptyForm: TenantFormData = {tenantName: '', description: '', timeZone: 'UTC'};

export default function TenantSettingsPage() {
	const toast = useToast();

	const [form, setForm] = useState<TenantFormData>(emptyForm);
	const [original, setOriginal] = useState<TenantFormData>(emptyForm);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [saving, setSaving] = useState(false);

	useOnceEffect(() => {
		getTenantUseCase.execute()
				.then((tenant) => {
					const data: TenantFormData = {
						tenantName: tenant.tenantName,
						description: tenant.description ?? '',
						timeZone: tenant.timeZone,
					};
					setForm(data);
					setOriginal(data);
				})
				.catch((err) => setError(err instanceof Error ? err.message : 'Failed to load tenant settings'))
				.finally(() => setLoading(false));
	});

	const update = (key: keyof TenantFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const handleSave = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			const input: UpdateTenantInput = {
				tenantName: form.tenantName,
				description: form.description || undefined,
				timeZone: form.timeZone,
			};
			await updateTenantUseCase.execute(input);
			setOriginal(form);
			toast.success('Tenant settings updated.');
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Terjadi kesalahan. Coba lagi.');
		} finally {
			setSaving(false);
		}
	};

	if (loading) return <Loading label="Loading tenant settings…"/>;

	return (
			<>
				<PageHeader title="Tenant setting" description="Update your laundry's business details"/>

				<Card style={{marginTop: 24, maxWidth: 520, marginLeft: 'auto', marginRight: 'auto'}}>
					{error ? (
							<p className="muted">{error}</p>
					) : (
							<form onSubmit={(e) => void handleSave(e)}>
								<Field label="Tenant name" htmlFor="tsName">
									<Input id="tsName" required value={form.tenantName} onChange={update('tenantName')}
									       placeholder="Business name" autoComplete="off"/>
								</Field>
								<Field label="Description" htmlFor="tsDescription">
									<Textarea id="tsDescription" value={form.description} onChange={update('description')}
									          placeholder="Short description of your business" rows={3} autoComplete="off"/>
								</Field>
								<Field label="Time zone" htmlFor="tsTimeZone" hint="Used for 'today', schedules and order numbers">
									<Select id="tsTimeZone" required value={form.timeZone} onChange={update('timeZone')}>
										{TIME_ZONES.map((zone) => (
												<option key={zone} value={zone}>{zone}</option>
										))}
									</Select>
								</Field>
								<div style={{display: 'flex', gap: 12, justifyContent: 'flex-end', marginTop: 20}}>
									<Button type="button" variant="ghost" onClick={() => setForm(original)}>Cancel</Button>
									<Button type="submit" disabled={saving}>
										{saving ? 'Saving…' : 'Save changes'}
									</Button>
								</div>
							</form>
					)}
				</Card>
			</>
	);
}
