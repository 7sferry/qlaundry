/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useCallback, useEffect, useRef, useState} from 'react';
import {Edit2, Mail, MapPin, Phone, Plus, Search, Star, Trash2, UserPlus, Users,} from 'lucide-react';
import {
	Button,
	Card,
	Drawer,
	Field,
	Input,
	Loading,
	Modal,
	PageHeader,
	Pagination,
	Select,
	StatCard,
	Textarea,
	useToast
} from '@/core/ui';
import type {SortBy, SortDirection} from '@/core/pagination/Pagination';
import {formatCurrency, formatDate} from '@/core/utils/format';
import {useCustomers} from '../useCustomers';
import type {Customer} from '../../domain/Customer';

const SORT_OPTIONS: { value: string; sortBy: SortBy; sortDir: SortDirection; label: string }[] = [
	{value: 'id-desc', sortBy: 'id', sortDir: 'desc', label: 'Newest first'},
	{value: 'id-asc', sortBy: 'id', sortDir: 'asc', label: 'Oldest first'},
	{value: 'name-asc', sortBy: 'name', sortDir: 'asc', label: 'Name A→Z'},
	{value: 'name-desc', sortBy: 'name', sortDir: 'desc', label: 'Name Z→A'},
];

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
				<Field label="Full name" htmlFor="cfName">
					<Input id="cfName" required value={form.fullName} onChange={update('fullName')}
					       placeholder="Customer name"/>
				</Field>
				<Field label="Telephone" htmlFor="cfPhone">
					<div className="input-with-icon">
						<Phone size={15}/>
						<Input id="cfPhone" type="tel" required value={form.phone} onChange={update('phone')}
					       placeholder="07XXXXXXXXX"/>
					</div>
				</Field>
				<Field label="Email" htmlFor="cfEmail">
					<div className="input-with-icon">
						<Mail size={15}/>
						<Input id="cfEmail" type="email" value={form.email} onChange={update('email')}
					       placeholder="optional"/>
					</div>
				</Field>
				<Field label="Address" htmlFor="cfAddr">
					<div className="input-with-icon">
						<MapPin size={15}/>
						<Input id="cfAddr" required value={form.address} onChange={update('address')}
					       placeholder="Full address"/>
					</div>
				</Field>
				<Field label="Notes" htmlFor="cfNotes">
					<Textarea id="cfNotes" value={form.notes} onChange={update('notes')} placeholder="optional" rows={2}/>
				</Field>
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Cancel</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Saving…' : editMode ? 'Save changes' : 'Add customer'}
					</Button>
				</div>
			</form>
	);
}

export default function CustomersPage() {
	const {
		customers, loading, hasNext, hasPrev, refresh, goNext, goPrevious, createCustomer, updateCustomer, deleteCustomer,
	} = useCustomers();
	const toast = useToast();

	const [search, setSearch] = useState('');
	const [sortBy, setSortBy] = useState<SortBy>('id');
	const [sortDir, setSortDir] = useState<SortDirection>('desc');
	const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [editMode, setEditMode] = useState(false);
	const [form, setForm] = useState<CustomerFormData>(emptyForm);
	const [saving, setSaving] = useState(false);

	const didMount = useRef(false);
	useEffect(() => {
		if (!didMount.current) {
			didMount.current = true;
			return;
		}
		const timer = setTimeout(() => {
			void refresh({search: search || undefined, sortBy, sortDir});
		}, 300);
		return () => clearTimeout(timer);
	}, [search, sortBy, sortDir, refresh]);

	const handleSortChange = useCallback((by: SortBy, dir: SortDirection) => {
		setSortBy(by);
		setSortDir(dir);
	}, []);

	if (loading && customers.length === 0) return <Loading label="Loading customers…"/>;

	const totalSpend = customers.reduce((s, c) => s + c.totalSpend, 0);

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
				toast.success('Customer updated.');
				setEditMode(false);
				setSelectedCustomer((prev) => prev ? {...prev, ...form} : null);
			} else {
				await createCustomer(form);
				toast.success('New customer added.');
				setShowAddModal(false);
			}
			setForm(emptyForm);
		} catch {
			toast.error('Something went wrong. Please try again.');
		} finally {
			setSaving(false);
		}
	};

	const handleDelete = async (c: Customer) => {
		if (!confirm(`Delete customer ${c.fullName}? This cannot be undone.`)) return;
		try {
			await deleteCustomer(c.id);
			toast.success(`${c.fullName} deleted.`);
			setSelectedCustomer(null);
		} catch {
			toast.error('Failed to delete customer.');
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
						title="Customer management"
						description="Manage your customer base"
						actions={
							<Button onClick={openAdd}>
								<UserPlus size={15}/> Add customer
							</Button>
						}
				/>

				<div className="grid grid--stats">
					<StatCard icon={<Users size={20}/>} value={customers.length} label="Customers" hint="On this page"/>
					<StatCard icon={<Star size={20}/>} value={formatCurrency(totalSpend)} label="Total spend"
					          hint="On this page"/>
					<StatCard
							icon={<Users size={20}/>}
							value={customers.length > 0 ? formatCurrency(Math.round(totalSpend / customers.length)) : 'Rp0'}
							label="Average spend"
							hint="On this page"
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
  								placeholder="Search name, phone, or email…"
								/>
							</div>
						</Field>
						<Field>
							<Select
									value={`${sortBy}-${sortDir}`}
									onChange={(e) => {
										const opt = SORT_OPTIONS.find((o) => o.value === e.target.value);
										if (opt) handleSortChange(opt.sortBy, opt.sortDir);
									}}
							>
								{SORT_OPTIONS.map((o) => (
										<option key={o.value} value={o.value}>{o.label}</option>
								))}
							</Select>
						</Field>
					</div>

					<div className="table-wrap">
						<table className="table">
							<thead>
							<tr>
								<th>Customer</th>
								<th>Contact</th>
								<th>Total orders</th>
								<th>Total spend</th>
								<th>Last order</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{customers.map((c) => (
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
   							<td>{c.totalOrders} orders</td>
										<td><strong>{formatCurrency(c.totalSpend)}</strong></td>
										<td>{c.lastOrderAt ? formatDate(c.lastOrderAt) : '—'}</td>
										<td onClick={(e) => e.stopPropagation()}>
											<div className="row" style={{gap: 4}}>
												<button className="icon-btn" onClick={() => openEdit(c)} title="Edit">
													<Edit2 size={14}/>
												</button>
    								<button className="icon-btn icon-btn--danger" onClick={() => void handleDelete(c)}
    								        title="Delete">
													<Trash2 size={14}/>
												</button>
											</div>
										</td>
									</tr>
							))}
							</tbody>
						</table>

						{!customers.length && (
 							<div className="empty-state">
 								<Users size={28}/>
 								<strong>No customers</strong>
 								<span>
 								{search ? 'Try changing the filters.' : 'Add your first customer.'}
 								</span>
 								{!search && (
 										<Button onClick={openAdd}><Plus size={14}/> Add customer</Button>
 								)}
 							</div>
						)}
					</div>

					<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={() => void goNext()} onPrev={() => void goPrevious()}
					            loading={loading}/>
				</Card>

				<Modal open={showAddModal} onClose={() => setShowAddModal(false)} title="Add new customer">
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
						subtitle={selectedCustomer ? `Joined ${formatDate(selectedCustomer.joinedAt)}` : ''}
						width="460px"
						footer={
								selectedCustomer && (
										<div className="row" style={{gap: 8, justifyContent: 'flex-end'}}>
   								<Button variant="danger" onClick={() => void handleDelete(selectedCustomer)}>
   									<Trash2 size={14}/> Delete
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
									</div>
								</div>

								<div className="grid grid--stats"
								     style={{gridTemplateColumns: 'repeat(2, 1fr)', gap: 12, margin: '20px 0'}}>
									<div className="mini-stat">
										<span className="mini-stat__value">{selectedCustomer.totalOrders}</span>
   							<span className="mini-stat__label">Total orders</span>
									</div>
									<div className="mini-stat">
										<span className="mini-stat__value">{formatCurrency(selectedCustomer.totalSpend)}</span>
   							<span className="mini-stat__label">Total spend</span>
									</div>
								</div>

								<div className="detail-section">
   						<h4>Contact</h4>
									<div className="detail-grid">
										<div>
   								<small>Telephone</small>
											<strong>{selectedCustomer.phone}</strong>
										</div>
										<div>
											<small>Email</small>
											<strong>{selectedCustomer.email ?? '—'}</strong>
										</div>
										<div style={{gridColumn: '1/-1'}}>
   								<small>Address</small>
											<strong>{selectedCustomer.address}</strong>
										</div>
									</div>
								</div>

								{selectedCustomer.notes && (
   							<div className="detail-section">
   								<h4>Notes</h4>
											<p className="muted" style={{fontSize: 13}}>{selectedCustomer.notes}</p>
										</div>
								)}

								<div className="detail-section">
  							<h4>Activity</h4>
									<div className="detail-grid">
										<div>
   								<small>Joined</small>
											<strong>{formatDate(selectedCustomer.joinedAt)}</strong>
										</div>
										<div>
   								<small>Last order</small>
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
