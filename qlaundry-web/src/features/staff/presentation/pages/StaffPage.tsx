/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {AtSign, Edit2, Lock, Mail, MapPin, Phone, Plus, Search, Trash2, UserPlus, Users,} from 'lucide-react';
import {
	Badge,
	Button,
	Card,
	Drawer,
	Field,
	Input,
	Loading,
	Modal,
	PageHeader,
	StatCard,
	Textarea,
	useToast
} from '@/core/ui';
import {formatDate} from '@/core/utils/format';
import {useStaff} from '../useStaff';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../../domain/Staff';

interface StaffFormData {
	username: string;
	password: string;
	fullName: string;
	description: string;
	email: string;
	phone: string;
	address: string;
}

const emptyForm: StaffFormData = {
	username: '',
	password: '',
	fullName: '',
	description: '',
	email: '',
	phone: '',
	address: '',
};

interface StaffFormProps {
	form: StaffFormData;
	update: (key: keyof StaffFormData) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
	onSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
	onCancel: () => void;
	saving: boolean;
	editMode: boolean;
}

function StaffForm({form, update, onSubmit, onCancel, saving, editMode}: StaffFormProps) {
	return (
			<form onSubmit={onSubmit} style={{display: 'flex', flexDirection: 'column', gap: 0}}>
				<Field label="Username" htmlFor="sfUsername">
					<div className="input-with-icon">
						<AtSign size={15}/>
						<Input id="sfUsername" autoComplete="off" required disabled={editMode} value={form.username}
						       onChange={update('username')} placeholder="username staf"/>
					</div>
				</Field>
				{!editMode && (
						<Field label="Password" htmlFor="sfPassword">
							<div className="input-with-icon">
								<Lock size={15}/>
								<Input id="sfPassword" type="password" required minLength={8} value={form.password}
								       onChange={update('password')} placeholder="minimal 8 karakter"/>
							</div>
						</Field>
				)}
				<Field label="Nama lengkap" htmlFor="sfName">
					<Input id="sfName" required value={form.fullName} onChange={update('fullName')}
					       placeholder="Nama staf" autoComplete="off"/>
				</Field>
				<Field label="Nomor telepon" htmlFor="sfPhone">
					<div className="input-with-icon">
						<Phone size={15}/>
						<Input id="sfPhone" type="tel" value={form.phone} onChange={update('phone')}
						       placeholder="08xxxxxxxxxx" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Email" htmlFor="sfEmail">
					<div className="input-with-icon">
						<Mail size={15}/>
						<Input id="sfEmail" type="email" required value={form.email} onChange={update('email')}
						       placeholder="xxx@xxx.xxx" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Alamat" htmlFor="sfAddr">
					<div className="input-with-icon">
						<MapPin size={15}/>
						<Input id="sfAddr" value={form.address} onChange={update('address')}
						       placeholder="opsional" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Deskripsi" htmlFor="sfDesc">
					<Textarea id="sfDesc" value={form.description} onChange={update('description')}
					          placeholder="tugas atau catatan, opsional" rows={2} autoComplete="off"/>
				</Field>
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Batal</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Menyimpan…' : editMode ? 'Simpan perubahan' : 'Tambah staf'}
					</Button>
				</div>
			</form>
	);
}

export default function StaffPage() {
	const {staff, loading, createStaff, updateStaff, deleteStaff} = useStaff();
	const toast = useToast();

	const [search, setSearch] = useState('');
	const [selectedStaff, setSelectedStaff] = useState<Staff | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [editMode, setEditMode] = useState(false);
	const [form, setForm] = useState<StaffFormData>(emptyForm);
	const [saving, setSaving] = useState(false);

	if (loading) return <Loading label="Memuat staf…"/>;

	const visible = staff.filter((s) => {
		const q = search.toLowerCase();
		return (
				!q ||
				s.fullName.toLowerCase().includes(q) ||
				s.username.toLowerCase().includes(q) ||
				s.phones.some((p) => p.includes(q)) ||
				s.emails.some((e) => e.toLowerCase().includes(q))
		);
	});

	const now = new Date();
	const newThisMonth = staff.filter((s) => {
		const joined = new Date(s.joinedAt);
		return joined.getMonth() === now.getMonth() && joined.getFullYear() === now.getFullYear();
	}).length;

	const openAdd = () => {
		setForm(emptyForm);
		setShowAddModal(true);
	};

	const openEdit = (s: Staff) => {
		setForm({
			username: s.username,
			password: '',
			fullName: s.fullName,
			description: s.description ?? '',
			email: s.emails[0] ?? '',
			phone: s.phones[0] ?? '',
			address: s.addresses[0] ?? '',
		});
		setEditMode(true);
		setSelectedStaff(s);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			if (editMode && selectedStaff) {
				const input: UpdateStaffInput = {
					id: selectedStaff.id,
					fullName: form.fullName,
					description: form.description || undefined,
					emails: form.email ? [form.email] : [],
					phones: form.phone ? [form.phone] : [],
					addresses: form.address ? [form.address] : [],
				};
				const updated = await updateStaff(input);
				toast.success('Data staf diperbarui.');
				setEditMode(false);
				setSelectedStaff(updated);
			} else {
				const input: CreateStaffInput = {
					username: form.username,
					password: form.password,
					fullName: form.fullName,
					description: form.description || undefined,
					emails: form.email ? [form.email] : [],
					phones: form.phone ? [form.phone] : [],
					addresses: form.address ? [form.address] : [],
				};
				await createStaff(input);
				toast.success('Staf baru berhasil ditambahkan.');
				setShowAddModal(false);
			}
			setForm(emptyForm);
		} catch {
			toast.error('Terjadi kesalahan. Coba lagi.');
		} finally {
			setSaving(false);
		}
	};

	const handleDelete = async (s: Staff) => {
		if (!confirm(`Hapus staf ${s.fullName}? Data ini tidak dapat dipulihkan.`)) return;
		try {
			await deleteStaff(s.id);
			toast.success(`${s.fullName} dihapus.`);
			setSelectedStaff(null);
		} catch {
			toast.error('Gagal menghapus staf.');
		}
	};

	const update = (key: keyof StaffFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const handleCancel = () => {
		setShowAddModal(false);
		setEditMode(false);
		setForm(emptyForm);
	};

	return (
			<>
				<PageHeader
						title="Manajemen staf"
						description={`${staff.length} staf terdaftar`}
						actions={
							<Button onClick={openAdd}>
								<UserPlus size={15}/> Tambah staf
							</Button>
						}
				/>

				<div className="grid grid--stats">
					<StatCard icon={<Users size={20}/>} value={staff.length} label="Total staf" hint="Terdaftar"/>
					<StatCard icon={<UserPlus size={20}/>} value={newThisMonth} label="Staf baru"
					          hint="Bulan ini"/>
				</div>

				<Card style={{marginTop: 24, marginBottom: 20}}>
					<div className="filters">
						<Field>
							<div className="input-with-icon">
								<Search size={15}/>
								<Input
										value={search}
										onChange={(e) => setSearch(e.target.value)}
										placeholder="Cari nama, username, nomor HP, atau email…"
								/>
							</div>
						</Field>
					</div>

					<div className="table-wrap">
						<table className="table">
							<thead>
							<tr>
								<th>Staf</th>
								<th>Kontak</th>
								<th>Alamat</th>
								<th>Bergabung</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{visible.map((s) => (
									<tr
											key={s.id}
											className="table-row--clickable"
											onClick={() => setSelectedStaff(s)}
									>
										<td>
											<div className="row" style={{gap: 10}}>
												<div className="customer-avatar">
													{s.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()}
												</div>
												<div>
													<strong>{s.fullName}</strong>
													<span className="table-sub">@{s.username}</span>
												</div>
											</div>
										</td>
										<td>
											<div>
												{s.phones[0] ?? '—'}
												<span className="table-sub">{s.emails[0] ?? '—'}</span>
											</div>
										</td>
										<td>{s.addresses[0] ?? '—'}</td>
										<td>{formatDate(s.joinedAt)}</td>
										<td onClick={(e) => e.stopPropagation()}>
											<div className="row" style={{gap: 4}}>
												<button className="icon-btn" onClick={() => openEdit(s)} title="Edit">
													<Edit2 size={14}/>
												</button>
												<button className="icon-btn icon-btn--danger" onClick={() => void handleDelete(s)}
												        title="Hapus">
													<Trash2 size={14}/>
												</button>
											</div>
										</td>
									</tr>
							))}
							</tbody>
						</table>

						{!visible.length && (
								<div className="empty-state">
									<Users size={28}/>
									<strong>Tidak ada staf</strong>
									<span>
                {search ? 'Coba ubah kata kunci pencarian.' : 'Tambahkan staf pertama Anda.'}
              </span>
									{!search && (
											<Button onClick={openAdd}><Plus size={14}/> Tambah staf</Button>
									)}
								</div>
						)}
					</div>
				</Card>

				<Modal open={showAddModal} onClose={() => setShowAddModal(false)} title="Tambah staf baru">
					<StaffForm
							form={form}
							update={update}
							onSubmit={handleSave}
							onCancel={handleCancel}
							saving={saving}
							editMode={editMode}
					/>
				</Modal>

				<Drawer
						open={!!selectedStaff && !editMode}
						onClose={() => setSelectedStaff(null)}
						title={selectedStaff?.fullName}
						subtitle={selectedStaff ? `Bergabung ${formatDate(selectedStaff.joinedAt)}` : ''}
						width="460px"
						footer={
								selectedStaff && (
										<div className="row" style={{gap: 8, justifyContent: 'flex-end'}}>
											<Button variant="danger" onClick={() => void handleDelete(selectedStaff)}>
												<Trash2 size={14}/> Hapus
											</Button>
											<Button onClick={() => openEdit(selectedStaff)}>
												<Edit2 size={14}/> Edit
											</Button>
										</div>
								)
						}
				>
					{selectedStaff && (
							<div className="customer-detail">
								<div className="customer-detail__hero">
									<div className="customer-avatar customer-avatar--lg">
										{selectedStaff.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()}
									</div>
									<div>
										<h3>{selectedStaff.fullName}</h3>
										<Badge tone="info"><AtSign size={11}/> {selectedStaff.username}</Badge>
									</div>
								</div>

								<div className="detail-section">
									<h4>Kontak</h4>
									<div className="detail-grid">
										<div>
											<small>Telepon</small>
											<strong>{selectedStaff.phones.length ? selectedStaff.phones.join(', ') : '—'}</strong>
										</div>
										<div>
											<small>Email</small>
											<strong>{selectedStaff.emails.length ? selectedStaff.emails.join(', ') : '—'}</strong>
										</div>
										<div style={{gridColumn: '1/-1'}}>
											<small>Alamat</small>
											<strong>{selectedStaff.addresses.length ? selectedStaff.addresses.join('; ') : '—'}</strong>
										</div>
									</div>
								</div>

								{selectedStaff.description && (
										<div className="detail-section">
											<h4>Deskripsi</h4>
											<p className="muted" style={{fontSize: 13}}>{selectedStaff.description}</p>
										</div>
								)}

								<div className="detail-section">
									<h4>Aktivitas</h4>
									<div className="detail-grid">
										<div>
											<small>Bergabung</small>
											<strong>{formatDate(selectedStaff.joinedAt)}</strong>
										</div>
									</div>
								</div>
							</div>
					)}
				</Drawer>

				<Modal open={editMode} onClose={() => {
					setEditMode(false);
					setForm(emptyForm);
				}} title={`Edit ${selectedStaff?.fullName}`}>
					<StaffForm
							form={form}
							update={update}
							onSubmit={handleSave}
							onCancel={handleCancel}
							saving={saving}
							editMode={editMode}
					/>
				</Modal>
			</>
	);
}
