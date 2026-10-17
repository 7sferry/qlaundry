/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {useMemo, useRef, useState} from 'react';
import {Users} from 'lucide-react';
import {Button, Input, Modal, Pagination} from '@/core/ui';
import type {OrderStaff, StaffOption} from '../../domain/OrderStaff';

const SEARCH_DEBOUNCE_MS = 300;

interface StaffPickerModalProps {
	open: boolean;
	mode: 'start' | 'change';
	orderNumber: string;
	currentUsername?: string;
	initialSelection: OrderStaff[];
	staffs: StaffOption[];
	loading: boolean;
	error: string | null;
	hasNext: boolean;
	hasPrev: boolean;
	submitting: boolean;
	onNext: () => void;
	onPrev: () => void;
	onSearch: (search?: string) => void;
	onCancel: () => void;
	onConfirm: (staffIds: string[]) => void;
}

export default function StaffPickerModal({
	open, mode, orderNumber, currentUsername, initialSelection, staffs, loading, error, hasNext, hasPrev, submitting,
	onNext, onPrev, onSearch, onCancel, onConfirm,
}: StaffPickerModalProps) {
	const [draft, setDraft] = useState<OrderStaff[]>(() => initialSelection.slice(0, 1));
	const [search, setSearch] = useState('');
	const searchTimer = useRef<ReturnType<typeof setTimeout> | undefined>(undefined);
	const draftIds = useMemo(() => new Set(draft.map((s) => s.staffId)), [draft]);

	const select = (staff: StaffOption) => setDraft([{staffId: staff.id, name: staff.fullName}]);

	const changeSearch = (value: string) => {
		setSearch(value);
		clearTimeout(searchTimer.current);
		searchTimer.current = setTimeout(() => onSearch(value.trim() || undefined), SEARCH_DEBOUNCE_MS);
	};

	const unchanged = () => mode === 'change'
			&& draft.length === initialSelection.length
			&& initialSelection.every((s) => draftIds.has(s.staffId));

	const confirm = () => {
		if (unchanged()) {
			onCancel();
			return;
		}
		onConfirm(draft.map((s) => s.staffId));
	};

	const assignee = draft[0]?.name ?? 'you';
	const confirmLabel = mode === 'start' ? `Start process (${assignee})` : `Save staff (${assignee})`;

	return (
			<Modal open={open} onClose={onCancel} size="sm"
			       title={mode === 'start' ? `Assign staff · ${orderNumber}` : `Staff · ${orderNumber}`}>
				<p className="muted" style={{fontSize: 13, marginTop: 0}}>
					Pick the staff working on this order. Defaults to you if none is picked.
				</p>
				<Input
						autoComplete="off"
						placeholder="Search staff by name…"
						value={search}
						onChange={(e) => changeSearch(e.target.value)}
						style={{marginBottom: 12}}
				/>
				{loading && staffs.length === 0 ? (
						<div className="center-box" style={{padding: '24px 0', minHeight: 0}}>
							<Users size={22} className="spin"/>
							<span>Loading staff…</span>
						</div>
				) : error ? (
						<div className="alert alert--error">{error}</div>
				) : staffs.length > 0 ? (
						<>
							<div className="staff-option-list">
								{staffs.map((staff) => (
										<label key={staff.id} className="staff-option">
											<input
													type="radio"
													name="order-staff"
													checked={draftIds.has(staff.id)}
													onChange={() => select(staff)}
											/>
											<div className="staff-option__info">
												<strong>{staff.fullName}{staff.username === currentUsername ? ' (you)' : ''}</strong>
											</div>
										</label>
								))}
							</div>
							{(hasNext || hasPrev) && (
									<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={onNext} onPrev={onPrev}
									            loading={loading}/>
							)}
						</>
				) : (
						<p className="muted" style={{fontSize: 13}}>No staff found.</p>
				)}
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 16}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Cancel</Button>
					<Button type="button" disabled={submitting} onClick={confirm}>
						{submitting ? 'Saving…' : confirmLabel}
					</Button>
				</div>
			</Modal>
	);
}
