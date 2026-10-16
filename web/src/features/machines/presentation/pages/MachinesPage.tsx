/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import React, {useCallback, useEffect, useRef, useState} from 'react';
import {Edit2, Plus, Search, WashingMachine} from 'lucide-react';
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
	Pagination,
	Select,
	Textarea,
	useToast,
} from '@/core/ui';
import {DEFAULT_PAGE_SIZE, PAGE_SIZE_OPTIONS, type SortDirection} from '@/core/pagination/Pagination';
import {useAuth} from '@/features/auth/presentation/useAuth';
import {useMachines} from '../useMachines';
import type {Machine, MachineType} from '../../domain/Machine';
import {MACHINE_TYPE_LABELS} from '../../domain/Machine';
import type {MachineSortBy} from '../../domain/MachineRepository';

const SORT_OPTIONS: { value: string; sortBy: MachineSortBy; sortDir: SortDirection; label: string }[] = [
	{value: 'name-asc', sortBy: 'name', sortDir: 'asc', label: 'Name A→Z'},
	{value: 'name-desc', sortBy: 'name', sortDir: 'desc', label: 'Name Z→A'},
	{value: 'id-desc', sortBy: 'id', sortDir: 'desc', label: 'Newest first'},
	{value: 'id-asc', sortBy: 'id', sortDir: 'asc', label: 'Oldest first'},
];

type StatusFilter = 'all' | 'active';

interface MachineFormData {
	name: string;
	type: MachineType;
	capacityKg: string;
	description: string;
	active: boolean;
}

const emptyForm: MachineFormData = {
	name: '',
	type: 'washer',
	capacityKg: '',
	description: '',
	active: true,
};

function typeTone(type: MachineType): BadgeTone {
	if (type === 'dryer') return 'warning';
	if (type === 'ironer') return 'info';
	return 'neutral';
}

interface MachineFormProps {
	form: MachineFormData;
	update: (key: keyof MachineFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => void;
	toggleActive: (e: React.ChangeEvent<HTMLInputElement>) => void;
	onSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
	onCancel: () => void;
	saving: boolean;
	editMode: boolean;
}

function MachineForm({form, update, toggleActive, onSubmit, onCancel, saving, editMode}: MachineFormProps) {
	return (
			<form onSubmit={onSubmit} style={{display: 'flex', flexDirection: 'column', gap: 0}}>
				<Field label="Machine name" htmlFor="mcName">
					<Input id="mcName" required autoComplete="off" value={form.name} onChange={update('name')}
					       placeholder="e.g. Mesin Cuci 1"/>
				</Field>
				<div className="row" style={{gap: 12}}>
					<Field label="Type" htmlFor="mcType">
						<Select id="mcType" required value={form.type} onChange={update('type')}>
							{(Object.keys(MACHINE_TYPE_LABELS) as MachineType[]).map((t) => (
									<option key={t} value={t}>{MACHINE_TYPE_LABELS[t]}</option>
							))}
						</Select>
					</Field>
					<Field label="Capacity (kg)" htmlFor="mcCapacity" hint="Optional">
						<Input id="mcCapacity" type="number" min="0.1" step="0.1" autoComplete="off"
						       value={form.capacityKg} onChange={update('capacityKg')} placeholder="8"/>
					</Field>
				</div>
				<Field label="Description" htmlFor="mcDesc">
					<Textarea id="mcDesc" value={form.description} onChange={update('description')}
					          placeholder="optional" rows={2}/>
				</Field>
				{editMode && (
						<label className="row" style={{gap: 6, fontSize: 13, marginTop: 4, marginBottom: 8}}>
							<input type="checkbox" checked={form.active} onChange={toggleActive}/>
							Active
						</label>
				)}
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Cancel</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Saving…' : editMode ? 'Save changes' : 'Add machine'}
					</Button>
				</div>
			</form>
	);
}

export default function MachinesPage() {
	const {
		machines, loading, hasNext, hasPrev, refresh, goNext, goPrevious, createMachine, updateMachine,
	} = useMachines();
	const {user} = useAuth();
	const toast = useToast();
	const canManage = user?.staffRole === 'SUPER_STAFF';

	const [search, setSearch] = useState('');
	const [typeFilter, setTypeFilter] = useState<'all' | MachineType>('all');
	const [statusFilter, setStatusFilter] = useState<StatusFilter>('all');
	const [sortBy, setSortBy] = useState<MachineSortBy>('name');
	const [sortDir, setSortDir] = useState<SortDirection>('asc');
	const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
	const [editingMachine, setEditingMachine] = useState<Machine | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [form, setForm] = useState<MachineFormData>(emptyForm);
	const [saving, setSaving] = useState(false);

	const lastFiltersKey = useRef<string | undefined>(undefined);
	useEffect(() => {
		const key = JSON.stringify({search, typeFilter, statusFilter, sortBy, sortDir, pageSize});
		if (lastFiltersKey.current === undefined || lastFiltersKey.current === key) {
			lastFiltersKey.current = key;
			return;
		}
		lastFiltersKey.current = key;
		const timer = setTimeout(() => {
			void refresh({
				search: search || undefined,
				type: typeFilter === 'all' ? undefined : typeFilter,
				activeOnly: statusFilter === 'active',
				sortBy,
				sortDir,
				pageSize,
			});
		}, 300);
		return () => clearTimeout(timer);
	}, [search, typeFilter, statusFilter, sortBy, sortDir, pageSize, refresh]);

	const handleSortChange = useCallback((by: MachineSortBy, dir: SortDirection) => {
		setSortBy(by);
		setSortDir(dir);
	}, []);

	if (loading && machines.length === 0) return <Loading label="Loading machines…"/>;

	const openAdd = () => {
		setForm(emptyForm);
		setShowAddModal(true);
	};

	const openEdit = (m: Machine) => {
		setForm({
			name: m.name,
			type: m.type,
			capacityKg: m.capacityKg === null ? '' : String(m.capacityKg),
			description: m.description,
			active: m.active,
		});
		setEditingMachine(m);
	};

	const handleCancel = () => {
		setShowAddModal(false);
		setEditingMachine(null);
		setForm(emptyForm);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			const input = {
				name: form.name,
				type: form.type,
				capacityKg: form.capacityKg === '' ? null : Number(form.capacityKg),
				description: form.description || undefined,
			};
			if (editingMachine) {
				await updateMachine({...input, id: editingMachine.id, active: form.active});
				toast.success('Machine updated.');
			} else {
				await createMachine(input);
				toast.success('New machine added.');
			}
			handleCancel();
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
		} finally {
			setSaving(false);
		}
	};

	const update = (key: keyof MachineFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const toggleActive = (e: React.ChangeEvent<HTMLInputElement>) =>
			setForm((prev) => ({...prev, active: e.target.checked}));

	const hasActiveFilters = search || typeFilter !== 'all' || statusFilter !== 'all';

	return (
			<>
				<PageHeader
						title="Machines"
						description="Washers, dryers and irons your orders run on"
						actions={
								canManage && (
										<Button onClick={openAdd}>
											<Plus size={15}/> Add machine
										</Button>
								)
						}
				/>

				<Card style={{marginBottom: 20}}>
					<div className="filters">
						<Field>
							<div className="input-with-icon">
								<Search size={15}/>
								<Input value={search} autoComplete="off" onChange={(e) => setSearch(e.target.value)}
								       placeholder="Search machine name…"/>
							</div>
						</Field>
						<Field>
							<Select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value as typeof typeFilter)}>
								<option value="all">All types</option>
								{(Object.keys(MACHINE_TYPE_LABELS) as MachineType[]).map((t) => (
										<option key={t} value={t}>{MACHINE_TYPE_LABELS[t]}</option>
								))}
							</Select>
						</Field>
						<Field>
							<Select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value as StatusFilter)}>
								<option value="all">All machines</option>
								<option value="active">Active only</option>
							</Select>
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
						<Field>
							<Select value={pageSize} onChange={(e) => setPageSize(Number(e.target.value))}
							        aria-label="Rows per page">
								{PAGE_SIZE_OPTIONS.map((size) => (
										<option key={size} value={size}>{size} / page</option>
								))}
							</Select>
						</Field>
					</div>

					<div className="table-wrap">
						<table className="table">
							<thead>
							<tr>
								<th>Machine</th>
								<th>Type</th>
								<th>Capacity</th>
								<th>Usage</th>
								<th>Status</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{machines.map((m) => (
									<tr key={m.id}>
										<td>
											<strong>{m.name}</strong>
											{m.description && <span className="table-sub">{m.description}</span>}
										</td>
										<td>
											<Badge tone={typeTone(m.type)}>{MACHINE_TYPE_LABELS[m.type]}</Badge>
										</td>
										<td>{m.capacityKg === null ? <span className="muted">—</span> : `${m.capacityKg} kg`}</td>
										<td>
											{m.inUse ? (
													<Badge tone="info">In use · {m.currentOrderNumber}</Badge>
											) : (
													<span className="muted" style={{fontSize: 12}}>Idle</span>
											)}
										</td>
										<td>
											<Badge tone={m.active ? 'success' : 'neutral'}>{m.active ? 'Active' : 'Inactive'}</Badge>
										</td>
										{canManage ? (
												<td>
													<button className="icon-btn" onClick={() => openEdit(m)} title="Edit">
														<Edit2 size={14}/>
													</button>
												</td>
										) : <td/>}
									</tr>
							))}
							</tbody>
						</table>

						{!machines.length && (
								<div className="empty-state">
									<WashingMachine size={28}/>
									<strong>No machines</strong>
									<span>{hasActiveFilters ? 'Try changing the filters.' : 'Add your first washer or dryer.'}</span>
									{!hasActiveFilters && canManage && (
											<Button onClick={openAdd}><Plus size={14}/> Add machine</Button>
									)}
								</div>
						)}
					</div>

					<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={() => void goNext()} onPrev={() => void goPrevious()}
					            loading={loading}/>
				</Card>

				<Modal open={showAddModal} onClose={handleCancel} title="Add new machine">
					<MachineForm form={form} update={update} toggleActive={toggleActive} onSubmit={handleSave}
					             onCancel={handleCancel} saving={saving} editMode={false}/>
				</Modal>

				<Modal open={editingMachine !== null} onClose={handleCancel}
				       title={`Edit ${editingMachine?.name ?? 'machine'}`}>
					<MachineForm form={form} update={update} toggleActive={toggleActive} onSubmit={handleSave}
					             onCancel={handleCancel} saving={saving} editMode={true}/>
				</Modal>
			</>
	);
}
