/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

import React, {useCallback, useEffect, useRef, useState} from 'react';
import {Clock, Edit2, Pause, Percent, Play, Plus, Search, Ticket, Users,} from 'lucide-react';
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
	StatCard,
	Textarea,
	useToast,
} from '@/core/ui';
import type {SortDirection} from '@/core/pagination/Pagination';
import {formatCurrency, formatDatetime} from '@/core/utils/format';
import {useAuth} from '@/features/auth/presentation/useAuth';
import {usePromotionList} from '../usePromotionList';
import type {Promotion, PromotionType} from '../../domain/Promotion';
import {PROMOTION_TYPE_LABELS} from '../../domain/Promotion';
import type {PromotionSortBy} from '../../domain/PromotionRepository';

const SORT_OPTIONS: { value: string; sortBy: PromotionSortBy; sortDir: SortDirection; label: string }[] = [
	{value: 'id-desc', sortBy: 'id', sortDir: 'desc', label: 'Newest first'},
	{value: 'id-asc', sortBy: 'id', sortDir: 'asc', label: 'Oldest first'},
	{value: 'end_at-asc', sortBy: 'end_at', sortDir: 'asc', label: 'Expiring soonest'},
	{value: 'end_at-desc', sortBy: 'end_at', sortDir: 'desc', label: 'Expiring latest'},
	{value: 'name-asc', sortBy: 'name', sortDir: 'asc', label: 'Name A→Z'},
	{value: 'name-desc', sortBy: 'name', sortDir: 'desc', label: 'Name Z→A'},
	{value: 'code-asc', sortBy: 'code', sortDir: 'asc', label: 'Code A→Z'},
	{value: 'code-desc', sortBy: 'code', sortDir: 'desc', label: 'Code Z→A'},
];

interface PromotionFormData {
	code: string;
	name: string;
	description: string;
	type: PromotionType;
	percentage: string;
	amount: string;
	maxDiscountAmount: string;
	minSubtotal: string;
	combinable: boolean;
	usageLimit: string;
	startAt: string;
	endAt: string;
	active: boolean;
}

const emptyForm: PromotionFormData = {
	code: '',
	name: '',
	description: '',
	type: 'cumulative_percentage',
	percentage: '',
	amount: '',
	maxDiscountAmount: '',
	minSubtotal: '',
	combinable: true,
	usageLimit: '',
	startAt: '',
	endAt: '',
	active: true,
};

function typeTone(type: PromotionType): BadgeTone {
	if (type === 'fixed_amount') return 'success';
	if (type === 'non_cumulative_percentage') return 'warning';
	return 'info';
}

function formatDiscount(p: Promotion): string {
	const base = p.type === 'fixed_amount' ? formatCurrency(p.amount ?? 0) : `${p.percentage ?? 0}%`;
	return p.maxDiscountAmount ? `${base} (max ${formatCurrency(p.maxDiscountAmount)})` : base;
}

function formatWindow(p: Promotion): string {
	if (p.startAt && p.endAt) return `${formatDatetime(p.startAt)} – ${formatDatetime(p.endAt)}`;
	if (p.endAt) return `Until ${formatDatetime(p.endAt)}`;
	if (p.startAt) return `From ${formatDatetime(p.startAt)}`;
	return 'No expiry';
}

/** `<input type="datetime-local">` wants `YYYY-MM-DDTHH:mm` in the viewer's local time, not the UTC `Promotion.startAt`/`endAt` ISO string — using local getters (not `.slice()` on the ISO string) is what keeps the picker showing the same wall-clock moment the promotion was actually saved with. */
function toDatetimeLocalValue(iso: string): string {
	const d = new Date(iso);
	const pad = (n: number) => String(n).padStart(2, '0');
	return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function isExpiringSoon(p: Promotion): boolean {
	if (!p.active || !p.endAt) return false;
	const msLeft = new Date(p.endAt).getTime() - Date.now();
	return msLeft > 0 && msLeft <= 7 * 24 * 60 * 60 * 1000;
}

interface PromotionFormProps {
	form: PromotionFormData;
	update: (key: keyof PromotionFormData) => (
		e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => void;
	updateCode: (e: React.ChangeEvent<HTMLInputElement>) => void;
	toggle: (key: 'combinable' | 'active') => (e: React.ChangeEvent<HTMLInputElement>) => void;
	onSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
	onCancel: () => void;
	saving: boolean;
	editMode: boolean;
}

function PromotionForm({form, update, updateCode, toggle, onSubmit, onCancel, saving, editMode}: PromotionFormProps) {
	const isFixedAmount = form.type === 'fixed_amount';
	return (
		<form onSubmit={onSubmit} style={{display: 'flex', flexDirection: 'column', gap: 0}}>
			<div className="row" style={{gap: 12}}>
				<Field label="Code" htmlFor="prCode">
					<Input id="prCode" required value={form.code} onChange={updateCode}
					       placeholder="e.g. LEBARAN25"/>
				</Field>
				<Field label="Name" htmlFor="prName">
					<Input id="prName" required value={form.name} onChange={update('name')}
					       placeholder="e.g. Diskon Lebaran"/>
				</Field>
			</div>
			<Field label="Description" htmlFor="prDesc">
				<Textarea id="prDesc" value={form.description} onChange={update('description')}
				          placeholder="optional" rows={2}/>
			</Field>
			<Field label="Type" htmlFor="prType">
				<Select id="prType" required value={form.type} onChange={update('type')}>
					{(Object.keys(PROMOTION_TYPE_LABELS) as PromotionType[]).map((t) => (
						<option key={t} value={t}>{PROMOTION_TYPE_LABELS[t]}</option>
					))}
				</Select>
			</Field>
			<div className="row" style={{gap: 12}}>
				{isFixedAmount ? (
					<Field label="Amount (Rp)" htmlFor="prAmount">
						<Input id="prAmount" type="number" min="1" step="1" required value={form.amount}
						       onChange={update('amount')} placeholder="5000"/>
					</Field>
				) : (
					<Field label="Percentage (%)" htmlFor="prPercentage">
						<Input id="prPercentage" type="number" min="0.01" max="100" step="0.01" required
						       value={form.percentage} onChange={update('percentage')} placeholder="25"/>
					</Field>
				)}
				<Field label="Max discount (Rp)" htmlFor="prMaxDiscount">
					<Input id="prMaxDiscount" type="number" min="1" step="1"
					       value={isFixedAmount ? '' : form.maxDiscountAmount}
					       onChange={update('maxDiscountAmount')}
					       placeholder={isFixedAmount ? 'n/a for fixed amount' : 'optional'}
					       disabled={isFixedAmount}/>
				</Field>
			</div>
			<div className="row" style={{gap: 12}}>
				<Field label="Min subtotal (Rp)" htmlFor="prMinSubtotal" hint="Optional eligibility floor">
					<Input id="prMinSubtotal" type="number" min="1" step="1" value={form.minSubtotal}
					       onChange={update('minSubtotal')} placeholder="optional"/>
				</Field>
				<Field label="Usage limit" htmlFor="prUsageLimit" hint="Optional, blank = unlimited">
					<Input id="prUsageLimit" type="number" min="1" step="1" value={form.usageLimit}
					       onChange={update('usageLimit')} placeholder="optional"/>
				</Field>
			</div>
			<div className="row" style={{gap: 12}}>
				<Field label="Start date & time" htmlFor="prStartAt">
					<Input id="prStartAt" type="datetime-local" required value={form.startAt} onChange={update('startAt')}/>
				</Field>
				<Field label="End date & time" htmlFor="prEndAt">
					<Input id="prEndAt" type="datetime-local" required value={form.endAt} onChange={update('endAt')}/>
				</Field>
			</div>
			<div className="row" style={{gap: 16, marginTop: 4, marginBottom: 8}}>
				<label className="row" style={{gap: 6, fontSize: 13}}>
					<input type="checkbox" checked={form.combinable} onChange={toggle('combinable')}/>
					Combinable with other promo codes
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
					{saving ? 'Saving…' : editMode ? 'Save changes' : 'Add promotion'}
				</Button>
			</div>
		</form>
	);
}

export default function PromotionsPage() {
	const {
		promotions, loading, hasNext, hasPrev, refresh, goNext, goPrevious,
		createPromotion, updatePromotion, togglePromotion,
	} = usePromotionList();
	const {user} = useAuth();
	const toast = useToast();
	const canManage = user?.staffRole === 'SUPER_STAFF';

	const [search, setSearch] = useState('');
	const [typeFilter, setTypeFilter] = useState<'all' | PromotionType>('all');
	const [sortBy, setSortBy] = useState<PromotionSortBy>('id');
	const [sortDir, setSortDir] = useState<SortDirection>('desc');
	const [selectedPromotion, setSelectedPromotion] = useState<Promotion | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [editMode, setEditMode] = useState(false);
	const [form, setForm] = useState<PromotionFormData>(emptyForm);
	const [saving, setSaving] = useState(false);

	// Compares against the *last processed* filter snapshot, not a boolean "have we mounted" flag —
	// two prior attempts at "skip the first run" both failed here. A boolean with no cleanup lets
	// React 18 StrictMode's dev-only mount double-invoke slip a second, real `refresh(...)` call
	// through on the phantom re-invocation (different query string than `usePaginatedList`'s own
	// mount fetch — a genuine double network hit). A boolean *with* a self-resetting cleanup avoids
	// that but permanently swallows every real filter change afterward, since React calls the last
	// returned cleanup before every re-run, not just StrictMode's synthetic one. Comparing values
	// avoids both: StrictMode's replay presents the *same* filters, so it's recognized as a no-op
	// and skipped without ever touching a cleanup; a genuine change always has different filters, so
	// it's never mistaken for a replay no matter how the ref got into its current state.
	const lastFiltersKey = useRef<string>();
	useEffect(() => {
		const key = JSON.stringify({search, typeFilter, sortBy, sortDir});
		if (lastFiltersKey.current === undefined || lastFiltersKey.current === key) {
			lastFiltersKey.current = key;
			return;
		}
		lastFiltersKey.current = key;
		const timer = setTimeout(() => {
			void refresh({
				search: search || undefined,
				type: typeFilter === 'all' ? undefined : typeFilter,
				sortBy,
				sortDir,
			});
		}, 300);
		return () => clearTimeout(timer);
	}, [search, typeFilter, sortBy, sortDir, refresh]);

	const handleSortChange = useCallback((by: PromotionSortBy, dir: SortDirection) => {
		setSortBy(by);
		setSortDir(dir);
	}, []);

	if (loading && promotions.length === 0) return <Loading label="Loading promotions…"/>;

	const activeCount = promotions.filter((p) => p.active).length;
	const expiringSoonCount = promotions.filter(isExpiringSoon).length;
	const totalRedemptions = promotions.reduce((sum, p) => sum + p.usedCount, 0);

	const openAdd = () => {
		setForm(emptyForm);
		setShowAddModal(true);
	};

	const openEdit = (p: Promotion) => {
		setForm({
			code: p.code,
			name: p.name,
			description: p.description ?? '',
			type: p.type,
			percentage: p.percentage !== undefined ? String(p.percentage) : '',
			amount: p.amount !== undefined ? String(p.amount) : '',
			maxDiscountAmount: p.maxDiscountAmount !== undefined ? String(p.maxDiscountAmount) : '',
			minSubtotal: p.minSubtotal !== undefined ? String(p.minSubtotal) : '',
			combinable: p.combinable,
			usageLimit: p.usageLimit !== undefined ? String(p.usageLimit) : '',
			startAt: p.startAt ? toDatetimeLocalValue(p.startAt) : '',
			endAt: p.endAt ? toDatetimeLocalValue(p.endAt) : '',
			active: p.active,
		});
		setEditMode(true);
		setSelectedPromotion(p);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			const input = {
				code: form.code,
				name: form.name,
				description: form.description || undefined,
				type: form.type,
				percentage: form.type === 'fixed_amount' ? undefined : Number(form.percentage),
				amount: form.type === 'fixed_amount' ? Number(form.amount) : undefined,
				maxDiscountAmount: form.type === 'fixed_amount' || !form.maxDiscountAmount
					? undefined : Number(form.maxDiscountAmount),
				minSubtotal: form.minSubtotal ? Number(form.minSubtotal) : undefined,
				combinable: form.combinable,
				usageLimit: form.usageLimit ? Number(form.usageLimit) : undefined,
				startAt: form.startAt,
				endAt: form.endAt,
			};
			if (editMode && selectedPromotion) {
				const updated = await updatePromotion({...input, id: selectedPromotion.id, active: form.active});
				toast.success('Promotion updated.');
				setEditMode(false);
				setSelectedPromotion(updated);
			} else {
				await createPromotion(input);
				toast.success('New promotion added.');
				setShowAddModal(false);
			}
			setForm(emptyForm);
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
		} finally {
			setSaving(false);
		}
	};

	const handleToggle = async (p: Promotion) => {
		try {
			await togglePromotion(p.id, !p.active);
			toast.success(p.active ? `${p.name} paused.` : `${p.name} resumed.`);
			setSelectedPromotion(null);
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Failed to update promotion.');
		}
	};

	const update = (key: keyof PromotionFormData) => (
		e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	/** `PromotionCodeDomain` upper-cases the code server-side too — mirroring it here so the form shows what will actually be saved. */
	const updateCode = (e: React.ChangeEvent<HTMLInputElement>) =>
		setForm((prev) => ({...prev, code: e.target.value.toUpperCase()}));

	const toggleCheckbox = (key: 'combinable' | 'active') => (e: React.ChangeEvent<HTMLInputElement>) =>
		setForm((prev) => ({...prev, [key]: e.target.checked}));

	const handleCancel = () => {
		setShowAddModal(false);
		setEditMode(false);
		setSelectedPromotion(null);
		setForm(emptyForm);
	};

	return (
		<>
			<PageHeader
				title="Promotions"
				description="Manage promo codes and discounts"
				actions={
					canManage && (
						<Button onClick={openAdd}>
							<Plus size={15}/> Add promotion
						</Button>
					)
				}
			/>

			<div className="grid grid--stats">
				<StatCard icon={<Ticket size={20}/>} value={promotions.length} label="Promotions" hint="On this page"/>
				<StatCard icon={<Percent size={20}/>} value={activeCount} label="Active" hint="On this page"/>
				<StatCard icon={<Clock size={20}/>} value={expiringSoonCount} label="Expiring soon"
				          hint="Within 7 days"/>
				<StatCard icon={<Users size={20}/>} value={totalRedemptions} label="Redemptions" hint="On this page"/>
			</div>

			<Card style={{marginTop: 24, marginBottom: 20}}>
				<div className="filters">
					<Field>
						<div className="input-with-icon">
							<Search size={15}/>
							<Input value={search} onChange={(e) => setSearch(e.target.value)}
							       placeholder="Search promotion name…"/>
						</div>
					</Field>
					<Field>
						<Select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value as typeof typeFilter)}>
							<option value="all">All types</option>
							{(Object.keys(PROMOTION_TYPE_LABELS) as PromotionType[]).map((t) => (
								<option key={t} value={t}>{PROMOTION_TYPE_LABELS[t]}</option>
							))}
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
				</div>

				<div className="table-wrap">
					<table className="table">
						<thead>
						<tr>
							<th>Code</th>
							<th>Type</th>
							<th>Discount</th>
							<th>Usage</th>
							<th>Window</th>
							<th>Status</th>
							<th/>
						</tr>
						</thead>
						<tbody>
						{promotions.map((p) => (
							<tr key={p.id} className="table-row--clickable" onClick={() => setSelectedPromotion(p)}>
								<td>
									<strong>{p.code}</strong>
									<span className="table-sub">{p.name}</span>
									{!p.combinable && <Badge tone="warning">Not combinable</Badge>}
								</td>
								<td>
									<Badge tone={typeTone(p.type)}>{PROMOTION_TYPE_LABELS[p.type]}</Badge>
								</td>
								<td>
									<strong>{formatDiscount(p)}</strong>
									{p.minSubtotal && (
										<span className="table-sub">Min spend {formatCurrency(p.minSubtotal)}</span>
									)}
								</td>
								<td>
									{p.usedCount}{p.usageLimit ? ` / ${p.usageLimit}` : ''}
									<span className="table-sub">
												{p.remainingUsage != null ? `${p.remainingUsage} left` : 'Unlimited'}
											</span>
								</td>
								<td>{formatWindow(p)}</td>
								<td>
									<Badge
										tone={p.active ? 'success' : 'neutral'}>{p.active ? 'Active' : 'Paused'}</Badge>
								</td>
								{canManage ? (
									<td onClick={(e) => e.stopPropagation()}>
										<div className="row" style={{gap: 4}}>
											<button className="icon-btn" onClick={() => openEdit(p)} title="Edit">
												<Edit2 size={14}/>
											</button>
											<button className="icon-btn" onClick={() => void handleToggle(p)}
											        title={p.active ? 'Pause' : 'Resume'}>
												{p.active ? <Pause size={14}/> : <Play size={14}/>}
											</button>
										</div>
									</td>
								) : <td/>}
							</tr>
						))}
						</tbody>
					</table>

					{!promotions.length && (
						<div className="empty-state">
							<Ticket size={28}/>
							<strong>No promotions</strong>
							<span>
									{search || typeFilter !== 'all'
										? 'Try changing the filters.'
										: 'Add your first promo code.'}
									</span>
							{!search && typeFilter === 'all' && canManage && (
								<Button onClick={openAdd}><Plus size={14}/> Add promotion</Button>
							)}
						</div>
					)}
				</div>

				<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={() => void goNext()}
				            onPrev={() => void goPrevious()}
				            loading={loading}/>
			</Card>

			<Modal open={showAddModal} onClose={handleCancel} title="Add new promotion">
				<PromotionForm form={form} update={update} updateCode={updateCode} toggle={toggleCheckbox}
				               onSubmit={handleSave}
				               onCancel={handleCancel} saving={saving} editMode={false}/>
			</Modal>

			<Modal open={editMode} onClose={handleCancel} title={`Edit ${selectedPromotion?.name ?? 'promotion'}`}>
				<PromotionForm form={form} update={update} updateCode={updateCode} toggle={toggleCheckbox}
				               onSubmit={handleSave}
				               onCancel={handleCancel} saving={saving} editMode={true}/>
			</Modal>
		</>
	);
}
