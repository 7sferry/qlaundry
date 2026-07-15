/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {Award, Edit2, Mail, MapPin, Phone, Plus, Search, Star, Trash2, UserPlus, Users,} from 'lucide-react';
import {
	Badge,
	type BadgeTone,
	Button,
	Card,
	Drawer,
	Field,
	Input,
	Loading,
	Modal,
	PageHeader,
	Select,
	StatCard,
	Textarea,
	useToast
} from '@/core/ui';
import {formatCurrency, formatDate} from '@/core/utils/format';
import {useCustomers} from '../useCustomers';
import type {Customer, CustomerTier} from '../../domain/Customer';
import {TIER_LABELS} from '../../domain/Customer';

function tierTone(tier: CustomerTier): BadgeTone {
	if (tier === 'platinum') return 'warning';
	if (tier === 'gold') return 'warning';
	if (tier === 'silver') return 'info';
	return 'neutral';
}

function tierIcon(tier: CustomerTier) {
	if (tier === 'platinum' || tier === 'gold') return <Star size={11} fill="currentColor"/>;
	if (tier === 'silver') return <Award size={11}/>;
	return null;
}

interface CustomerFormData {
	fullName: string;
	phone: string;
	email: string;
	address: string;
	notes: string;
}

const emptyForm: CustomerFormData = {
	fullName: '',
	phone: '',
	email: '',
	address: '',
	notes: '',
};

interface CustomerFormProps {
	form: CustomerFormData;
	update: (key: keyof CustomerFormData) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
	onSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
	onCancel: () => void;
	saving: boolean;
	editMode: boolean;
}

function CustomerForm({form, update, onSubmit, onCancel, saving, editMode}: CustomerFormProps) {
	return (
			<form onSubmit={onSubmit} style={{display: 'flex', flexDirection: 'column', gap: 0}}>
				<Field label="Nama lengkap" htmlFor="cfName">
					<Input id="cfName" required value={form.fullName} onChange={update('fullName')}
					       placeholder="Nama pelanggan"/>
				</Field>
				<Field label="Nomor telepon" htmlFor="cfPhone">
					<div className="input-with-icon">
						<Phone size={15}/>
						<Input id="cfPhone" type="tel" required value={form.phone} onChange={update('phone')}
						       placeholder="08xxxxxxxxxx"/>
					</div>
				</Field>
				<Field label="Email" htmlFor="cfEmail">
					<div className="input-with-icon">
						<Mail size={15}/>
						<Input id="cfEmail" type="email" value={form.email} onChange={update('email')}
						       placeholder="opsional"/>
					</div>
				</Field>
				<Field label="Alamat" htmlFor="cfAddr">
					<div className="input-with-icon">
						<MapPin size={15}/>
						<Input id="cfAddr" required value={form.address} onChange={update('address')}
						       placeholder="Alamat lengkap"/>
					</div>
				</Field>
				<Field label="Catatan" htmlFor="cfNotes">
					<Textarea id="cfNotes" value={form.notes} onChange={update('notes')} placeholder="opsional" rows={2}/>
				</Field>
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Batal</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Menyimpan…' : editMode ? 'Simpan perubahan' : 'Tambah pelanggan'}
					</Button>
				</div>
			</form>
	);
}

export default function CustomersPage() {
	const {customers, loading, createCustomer, updateCustomer, deleteCustomer} = useCustomers();
	const toast = useToast();

	const [search, setSearch] = useState('');
	const [tierFilter, setTierFilter] = useState<'all' | CustomerTier>('all');
	const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [editMode, setEditMode] = useState(false);
	const [form, setForm] = useState<CustomerFormData>(emptyForm);
	const [saving, setSaving] = useState(false);

	if (loading) return <Loading label="Memuat pelanggan…"/>;

	const visible = customers.filter((c) => {
		const q = search.toLowerCase();
		const matchSearch =
				!q ||
				c.fullName.toLowerCase().includes(q) ||
				c.phone.includes(q) ||
				c.email?.toLowerCase().includes(q);
		const matchTier = tierFilter === 'all' || c.tier === tierFilter;
		return matchSearch && matchTier;
	});

	const totalSpend = customers.reduce((s, c) => s + c.totalSpend, 0);
	const platinumCount = customers.filter((c) => c.tier === 'platinum').length;

	const openAdd = () => {
		setForm(emptyForm);
		setShowAddModal(true);
	};

	const openEdit = (c: Customer) => {
		setForm({
			fullName: c.fullName,
			phone: c.phone,
			email: c.email ?? '',
			address: c.address,
			notes: c.notes ?? '',
		});
		setEditMode(true);
		setSelectedCustomer(c);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			if (editMode && selectedCustomer) {
				await updateCustomer({id: selectedCustomer.id, ...form});
				toast.success('Data pelanggan diperbarui.');
				setEditMode(false);
				setSelectedCustomer((prev) => prev ? {...prev, ...form} : null);
			} else {
				await createCustomer(form);
				toast.success('Pelanggan baru berhasil ditambahkan.');
				setShowAddModal(false);
			}
			setForm(emptyForm);
		} catch {
			toast.error('Terjadi kesalahan. Coba lagi.');
		} finally {
			setSaving(false);
		}
	};

	const handleDelete = async (c: Customer) => {
		if (!confirm(`Hapus pelanggan ${c.fullName}? Data ini tidak dapat dipulihkan.`)) return;
		try {
			await deleteCustomer(c.id);
			toast.success(`${c.fullName} dihapus.`);
			setSelectedCustomer(null);
		} catch {
			toast.error('Gagal menghapus pelanggan.');
		}
	};

	const update = (key: keyof CustomerFormData) => (
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
						title="Manajemen pelanggan"
						description={`${customers.length} pelanggan terdaftar`}
						actions={
							<Button onClick={openAdd}>
								<UserPlus size={15}/> Tambah pelanggan
							</Button>
						}
				/>

				<div className="grid grid--stats">
					<StatCard icon={<Users size={20}/>} value={customers.length} label="Total pelanggan" hint="Terdaftar"/>
					<StatCard icon={<Award size={20}/>} value={platinumCount} label="Pelanggan platinum"
					          hint="Loyalitas tertinggi"/>
					<StatCard icon={<Star size={20}/>} value={formatCurrency(totalSpend)} label="Total pengeluaran"
					          hint="Seluruh pelanggan"/>
					<StatCard
							icon={<Users size={20}/>}
							value={customers.length > 0 ? formatCurrency(Math.round(totalSpend / customers.length)) : 'Rp0'}
							label="Rata-rata spend"
							hint="Per pelanggan"
					/>
				</div>

				<Card style={{marginTop: 24, marginBottom: 20}}>
					<div className="filters">
						<Field>
							<div className="input-with-icon">
								<Search size={15}/>
								<Input
										value={search}
										onChange={(e) => setSearch(e.target.value)}
										placeholder="Cari nama, nomor HP, atau email…"
								/>
							</div>
						</Field>
						<Field>
							<Select value={tierFilter} onChange={(e) => setTierFilter(e.target.value as typeof tierFilter)}>
								<option value="all">Semua tier</option>
								{(['bronze', 'silver', 'gold', 'platinum'] as CustomerTier[]).map((t) => (
										<option key={t} value={t}>{TIER_LABELS[t]}</option>
								))}
							</Select>
						</Field>
					</div>

					<div className="table-wrap">
						<table className="table">
							<thead>
							<tr>
								<th>Pelanggan</th>
								<th>Kontak</th>
								<th>Tier</th>
								<th>Total order</th>
								<th>Total pengeluaran</th>
								<th>Order terakhir</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{visible.map((c) => (
									<tr
											key={c.id}
											className="table-row--clickable"
											onClick={() => setSelectedCustomer(c)}
									>
										<td>
											<div className="row" style={{gap: 10}}>
												<div className="customer-avatar">
													{c.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()}
												</div>
												<div>
													<strong>{c.fullName}</strong>
													<span className="table-sub">{c.email ?? '—'}</span>
												</div>
											</div>
										</td>
										<td>{c.phone}</td>
										<td>
											<Badge tone={tierTone(c.tier)}>
												{tierIcon(c.tier)} {TIER_LABELS[c.tier]}
											</Badge>
										</td>
										<td>{c.totalOrders} order</td>
										<td><strong>{formatCurrency(c.totalSpend)}</strong></td>
										<td>{c.lastOrderAt ? formatDate(c.lastOrderAt) : '—'}</td>
										<td onClick={(e) => e.stopPropagation()}>
											<div className="row" style={{gap: 4}}>
												<button className="icon-btn" onClick={() => openEdit(c)} title="Edit">
													<Edit2 size={14}/>
												</button>
												<button className="icon-btn icon-btn--danger" onClick={() => void handleDelete(c)}
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
									<strong>Tidak ada pelanggan</strong>
									<span>
                {search || tierFilter !== 'all'
		                ? 'Coba ubah filter pencarian.'
		                : 'Tambahkan pelanggan pertama Anda.'}
              </span>
									{!search && tierFilter === 'all' && (
											<Button onClick={openAdd}><Plus size={14}/> Tambah pelanggan</Button>
									)}
								</div>
						)}
					</div>
				</Card>

				<Modal open={showAddModal} onClose={() => setShowAddModal(false)} title="Tambah pelanggan baru">
					<CustomerForm
							form={form}
							update={update}
							onSubmit={handleSave}
							onCancel={handleCancel}
							saving={saving}
							editMode={editMode}
					/>
				</Modal>

				<Drawer
						open={!!selectedCustomer && !editMode}
						onClose={() => setSelectedCustomer(null)}
						title={selectedCustomer?.fullName}
						subtitle={selectedCustomer ? `Bergabung ${formatDate(selectedCustomer.joinedAt)}` : ''}
						width="460px"
						footer={
								selectedCustomer && (
										<div className="row" style={{gap: 8, justifyContent: 'flex-end'}}>
											<Button variant="danger" onClick={() => void handleDelete(selectedCustomer)}>
												<Trash2 size={14}/> Hapus
											</Button>
											<Button onClick={() => openEdit(selectedCustomer)}>
												<Edit2 size={14}/> Edit
											</Button>
										</div>
								)
						}
				>
					{selectedCustomer && (
							<div className="customer-detail">
								<div className="customer-detail__hero">
									<div className="customer-avatar customer-avatar--lg">
										{selectedCustomer.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()}
									</div>
									<div>
										<h3>{selectedCustomer.fullName}</h3>
										<Badge tone={tierTone(selectedCustomer.tier)}>
											{tierIcon(selectedCustomer.tier)} {TIER_LABELS[selectedCustomer.tier]}
										</Badge>
									</div>
								</div>

								<div className="grid grid--stats"
								     style={{gridTemplateColumns: 'repeat(2, 1fr)', gap: 12, margin: '20px 0'}}>
									<div className="mini-stat">
										<span className="mini-stat__value">{selectedCustomer.totalOrders}</span>
										<span className="mini-stat__label">Total order</span>
									</div>
									<div className="mini-stat">
										<span className="mini-stat__value">{formatCurrency(selectedCustomer.totalSpend)}</span>
										<span className="mini-stat__label">Total belanja</span>
									</div>
								</div>

								<div className="detail-section">
									<h4>Kontak</h4>
									<div className="detail-grid">
										<div>
											<small>Telepon</small>
											<strong>{selectedCustomer.phone}</strong>
										</div>
										<div>
											<small>Email</small>
											<strong>{selectedCustomer.email ?? '—'}</strong>
										</div>
										<div style={{gridColumn: '1/-1'}}>
											<small>Alamat</small>
											<strong>{selectedCustomer.address}</strong>
										</div>
									</div>
								</div>

								{selectedCustomer.notes && (
										<div className="detail-section">
											<h4>Catatan</h4>
											<p className="muted" style={{fontSize: 13}}>{selectedCustomer.notes}</p>
										</div>
								)}

								<div className="detail-section">
									<h4>Aktivitas</h4>
									<div className="detail-grid">
										<div>
											<small>Bergabung</small>
											<strong>{formatDate(selectedCustomer.joinedAt)}</strong>
										</div>
										<div>
											<small>Order terakhir</small>
											<strong>{selectedCustomer.lastOrderAt ? formatDate(selectedCustomer.lastOrderAt) : '—'}</strong>
										</div>
									</div>
								</div>
							</div>
					)}
				</Drawer>

				<Modal open={editMode} onClose={() => {
					setEditMode(false);
					setForm(emptyForm);
				}} title={`Edit ${selectedCustomer?.fullName}`}>
					<CustomerForm
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
