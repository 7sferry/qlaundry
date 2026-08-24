/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import React, {useState} from 'react';
import {Clock, Edit2, ListChecks, Plus, Search, Sparkles, Star, Trash2, WashingMachine,} from 'lucide-react';
import {
	Badge,
	type BadgeTone,
	Button,
	Card,
	Field,
	Input,
	Loading,
	Modal,
	PageHeader,
	Select,
	StatCard,
	Textarea,
	useToast,
} from '@/core/ui';
import {formatCurrency} from '@/core/utils/format';
import {useAuth} from '@/features/auth/presentation/useAuth';
import {useServices} from '../useServices';
import type {LaundryService, ServiceCategory, ServiceUnit} from '../../domain/Service';
import {SERVICE_CATEGORY_LABELS, SERVICE_UNIT_LABELS} from '../../domain/Service';

interface ServiceFormData {
	name: string;
	description: string;
	category: ServiceCategory;
	unit: ServiceUnit;
	pricePerUnit: string;
	estimatedHours: string;
	expressMultiplier: string;
	popular: boolean;
	active: boolean;
}

const emptyForm: ServiceFormData = {
	name: '',
	description: '',
	category: 'wash',
	unit: 'kg',
	pricePerUnit: '',
	estimatedHours: '',
	expressMultiplier: '1.5',
	popular: false,
	active: true,
};

function categoryTone(category: ServiceCategory): BadgeTone {
	if (category === 'specialty') return 'warning';
	if (category === 'dry_clean') return 'info';
	return 'neutral';
}

interface ServiceFormProps {
	form: ServiceFormData;
	update: (key: keyof ServiceFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => void;
	toggle: (key: 'popular' | 'active') => (e: React.ChangeEvent<HTMLInputElement>) => void;
	onSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
	onCancel: () => void;
	saving: boolean;
	editMode: boolean;
}

function ServiceForm({form, update, toggle, onSubmit, onCancel, saving, editMode}: ServiceFormProps) {
	return (
			<form onSubmit={onSubmit} style={{display: 'flex', flexDirection: 'column', gap: 0}}>
				<Field label="Service name" htmlFor="svName">
					<Input id="svName" required value={form.name} onChange={update('name')}
					       placeholder="e.g. Wash & Fold"/>
				</Field>
				<Field label="Description" htmlFor="svDesc">
					<Textarea id="svDesc" value={form.description} onChange={update('description')}
					          placeholder="optional" rows={2}/>
				</Field>
				<div className="row" style={{gap: 12}}>
					<Field label="Category" htmlFor="svCategory">
						<Select id="svCategory" required value={form.category} onChange={update('category')}>
							{(Object.keys(SERVICE_CATEGORY_LABELS) as ServiceCategory[]).map((c) => (
									<option key={c} value={c}>{SERVICE_CATEGORY_LABELS[c]}</option>
							))}
						</Select>
					</Field>
					<Field label="Priced by" htmlFor="svUnit">
						<Select id="svUnit" required value={form.unit} onChange={update('unit')}>
							{(Object.keys(SERVICE_UNIT_LABELS) as ServiceUnit[]).map((u) => (
									<option key={u} value={u}>{SERVICE_UNIT_LABELS[u]}</option>
							))}
						</Select>
					</Field>
				</div>
				<div className="row" style={{gap: 12}}>
					<Field label="Price per unit (Rp)" htmlFor="svPrice">
						<Input id="svPrice" type="number" min="1" step="1" required value={form.pricePerUnit}
						       onChange={update('pricePerUnit')} placeholder="8000"/>
					</Field>
					<Field label="Estimated hours" htmlFor="svHours">
						<Input id="svHours" type="number" min="1" step="1" required value={form.estimatedHours}
						       onChange={update('estimatedHours')} placeholder="24"/>
					</Field>
				</div>
				<Field label="Express multiplier" htmlFor="svMultiplier"
				       hint="Applied to the price when a customer orders express priority">
					<Input id="svMultiplier" type="number" min="1" step="0.1" value={form.expressMultiplier}
					       onChange={update('expressMultiplier')} placeholder="1.5"/>
				</Field>
				<div className="row" style={{gap: 16, marginTop: 4, marginBottom: 8}}>
					<label className="row" style={{gap: 6, fontSize: 13}}>
						<input type="checkbox" checked={form.popular} onChange={toggle('popular')}/>
						Mark as popular
					</label>
					{editMode && (
							<label className="row" style={{gap: 6, fontSize: 13}}>
								<input type="checkbox" checked={form.active} onChange={toggle('active')}/>
								Active
							</label>
					)}
				</div>
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Cancel</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Saving…' : editMode ? 'Save changes' : 'Add service'}
					</Button>
				</div>
			</form>
	);
}

export default function ServicesPage() {
	const {services, loading, createService, updateService, deleteService} = useServices();
	const {user} = useAuth();
	const toast = useToast();
	const canManage = user?.staffRole === 'SUPER_STAFF';

	const [search, setSearch] = useState('');
	const [categoryFilter, setCategoryFilter] = useState<'all' | ServiceCategory>('all');
	const [selectedService, setSelectedService] = useState<LaundryService | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [editMode, setEditMode] = useState(false);
	const [form, setForm] = useState<ServiceFormData>(emptyForm);
	const [saving, setSaving] = useState(false);

	if (loading) return <Loading label="Loading services…"/>;

	const visible = services.filter((s) => {
		const q = search.toLowerCase();
		const matchSearch = !q || s.name.toLowerCase().includes(q);
		const matchCategory = categoryFilter === 'all' || s.category === categoryFilter;
		return matchSearch && matchCategory;
	});

	const activeCount = services.filter((s) => s.active).length;
	const popularCount = services.filter((s) => s.popular).length;
	const avgPrice = services.length
			? Math.round(services.reduce((sum, s) => sum + s.pricePerUnit, 0) / services.length)
			: 0;

	const openAdd = () => {
		setForm(emptyForm);
		setShowAddModal(true);
	};

	const openEdit = (s: LaundryService) => {
		setForm({
			name: s.name,
			description: s.description,
			category: s.category,
			unit: s.unit,
			pricePerUnit: String(s.pricePerUnit),
			estimatedHours: String(s.estimatedHours),
			expressMultiplier: String(s.expressMultiplier),
			popular: s.popular,
			active: s.active,
		});
		setEditMode(true);
		setSelectedService(s);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			const input = {
				name: form.name,
				description: form.description || undefined,
				pricePerUnit: Number(form.pricePerUnit),
				unit: form.unit,
				category: form.category,
				estimatedHours: Number(form.estimatedHours),
				expressMultiplier: form.expressMultiplier ? Number(form.expressMultiplier) : undefined,
				popular: form.popular,
			};
			if (editMode && selectedService) {
				const updated = await updateService({...input, id: selectedService.id, active: form.active});
				toast.success('Service updated.');
				setEditMode(false);
				setSelectedService(updated);
			} else {
				await createService(input);
				toast.success('New service added.');
				setShowAddModal(false);
			}
			setForm(emptyForm);
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
		} finally {
			setSaving(false);
		}
	};

	const handleDelete = async (s: LaundryService) => {
		if (!confirm(`Delete service "${s.name}"? This cannot be undone.`)) return;
		try {
			await deleteService(s.id);
			toast.success(`${s.name} deleted.`);
			setSelectedService(null);
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Failed to delete service.');
		}
	};

	const update = (key: keyof ServiceFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const toggle = (key: 'popular' | 'active') => (e: React.ChangeEvent<HTMLInputElement>) =>
			setForm((prev) => ({...prev, [key]: e.target.checked}));

	const handleCancel = () => {
		setShowAddModal(false);
		setEditMode(false);
		setSelectedService(null);
		setForm(emptyForm);
	};

	return (
			<>
				<PageHeader
						title="Laundry service menu"
						description={`${services.length} services in the price list`}
						actions={
								canManage && (
										<Button onClick={openAdd}>
											<Plus size={15}/> Add service
										</Button>
								)
						}
				/>

				<div className="grid grid--stats">
					<StatCard icon={<ListChecks size={20}/>} value={services.length} label="Total services"
					          hint="In the price list"/>
					<StatCard icon={<WashingMachine size={20}/>} value={activeCount} label="Active"
					          hint="Bookable by staff"/>
					<StatCard icon={<Star size={20}/>} value={popularCount} label="Popular" hint="Highlighted"/>
					<StatCard icon={<Sparkles size={20}/>} value={formatCurrency(avgPrice)} label="Average price"
					          hint="Per unit"/>
				</div>

				<Card style={{marginTop: 24, marginBottom: 20}}>
					<div className="filters">
						<Field>
							<div className="input-with-icon">
								<Search size={15}/>
								<Input value={search} onChange={(e) => setSearch(e.target.value)}
								       placeholder="Search service name…"/>
							</div>
						</Field>
						<Field>
							<Select value={categoryFilter}
							        onChange={(e) => setCategoryFilter(e.target.value as typeof categoryFilter)}>
								<option value="all">All categories</option>
								{(Object.keys(SERVICE_CATEGORY_LABELS) as ServiceCategory[]).map((c) => (
										<option key={c} value={c}>{SERVICE_CATEGORY_LABELS[c]}</option>
								))}
							</Select>
						</Field>
					</div>

					<div className="table-wrap">
						<table className="table">
							<thead>
							<tr>
								<th>Service</th>
								<th>Category</th>
								<th>Price</th>
								<th>Est. time</th>
								<th>Status</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{visible.map((s) => (
									<tr key={s.id} className="table-row--clickable" onClick={() => setSelectedService(s)}>
										<td>
											<div className="row" style={{gap: 6}}>
												<strong>{s.name}</strong>
												{s.popular && <Star size={13} fill="currentColor"/>}
											</div>
											{s.description && <span className="table-sub">{s.description}</span>}
										</td>
										<td>
											<Badge tone={categoryTone(s.category)}>{SERVICE_CATEGORY_LABELS[s.category]}</Badge>
										</td>
										<td>
											<strong>{formatCurrency(s.pricePerUnit)}</strong>
											<span className="table-sub"> / {SERVICE_UNIT_LABELS[s.unit].toLowerCase()}</span>
										</td>
										<td>
											<div className="row" style={{gap: 4}}>
												<Clock size={13}/> {s.estimatedHours}h
											</div>
										</td>
										<td>
											<Badge tone={s.active ? 'success' : 'neutral'}>{s.active ? 'Active' : 'Inactive'}</Badge>
										</td>
										{canManage ? (
												<td onClick={(e) => e.stopPropagation()}>
													<div className="row" style={{gap: 4}}>
														<button className="icon-btn" onClick={() => openEdit(s)} title="Edit">
															<Edit2 size={14}/>
														</button>
														<button className="icon-btn icon-btn--danger" onClick={() => void handleDelete(s)}
														        title="Delete">
															<Trash2 size={14}/>
														</button>
													</div>
												</td>
										) : <td/>}
									</tr>
							))}
							</tbody>
						</table>

						{!visible.length && (
								<div className="empty-state">
									<ListChecks size={28}/>
									<strong>No services</strong>
									<span>
									{search || categoryFilter !== 'all'
											? 'Try changing the filters.'
											: 'Add your first laundry service.'}
									</span>
									{!search && categoryFilter === 'all' && canManage && (
											<Button onClick={openAdd}><Plus size={14}/> Add service</Button>
									)}
								</div>
						)}
					</div>
				</Card>

				<Modal open={showAddModal} onClose={handleCancel} title="Add new service">
					<ServiceForm form={form} update={update} toggle={toggle} onSubmit={handleSave}
					             onCancel={handleCancel} saving={saving} editMode={false}/>
				</Modal>

				<Modal open={editMode} onClose={handleCancel} title={`Edit ${selectedService?.name ?? 'service'}`}>
					<ServiceForm form={form} update={update} toggle={toggle} onSubmit={handleSave}
					             onCancel={handleCancel} saving={saving} editMode={true}/>
				</Modal>
			</>
	);
}
