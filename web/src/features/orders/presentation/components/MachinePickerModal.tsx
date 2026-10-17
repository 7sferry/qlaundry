/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {useMemo, useState} from 'react';
import {WashingMachine} from 'lucide-react';
import {Button, Modal, Pagination} from '@/core/ui';
import type {MachineOption, MachineType, OrderMachine} from '../../domain/Machine';
import {MACHINE_TYPE_LABELS} from '../../domain/Machine';

type TypeFilter = 'all' | MachineType;

const TYPE_FILTERS: TypeFilter[] = ['all', ...(Object.keys(MACHINE_TYPE_LABELS) as MachineType[])];

interface MachinePickerModalProps {
	open: boolean;
	mode: 'start' | 'change';
	orderNumber: string;
	initialSelection: OrderMachine[];
	machines: MachineOption[];
	loading: boolean;
	error: string | null;
	hasNext: boolean;
	hasPrev: boolean;
	hasAnyMachine: boolean | null;
	submitting: boolean;
	onNext: () => void;
	onPrev: () => void;
	onTypeChange: (type?: MachineType) => void;
	onCancel: () => void;
	onConfirm: (machineIds: string[]) => void;
}

export default function MachinePickerModal({
	open, mode, orderNumber, initialSelection, machines, loading, error, hasNext, hasPrev, hasAnyMachine, submitting,
	onNext, onPrev, onTypeChange, onCancel, onConfirm,
}: MachinePickerModalProps) {
	const [draft, setDraft] = useState<OrderMachine[]>(() => initialSelection);
	const [typeFilter, setTypeFilter] = useState<TypeFilter>('all');
	const [confirmingEmpty, setConfirmingEmpty] = useState(false);
	const draftIds = useMemo(() => new Set(draft.map((m) => m.machineId)), [draft]);

	const busyElsewhere = (machine: MachineOption) =>
			machine.inUse && machine.currentOrderNumber !== orderNumber && !draftIds.has(machine.id);

	const toggle = (machine: MachineOption) =>
			setDraft((prev) => (
					prev.some((m) => m.machineId === machine.id)
							? prev.filter((m) => m.machineId !== machine.id)
							: [...prev, {machineId: machine.id, name: machine.name, type: machine.type}]
			));

	const changeType = (type: TypeFilter) => {
		setTypeFilter(type);
		onTypeChange(type === 'all' ? undefined : type);
	};

	const submit = () => onConfirm(draft.map((m) => m.machineId));

	const unchanged = () => mode === 'change'
			&& draft.length === initialSelection.length
			&& initialSelection.every((m) => draftIds.has(m.machineId));

	const confirm = () => {
		if (unchanged()) {
			onCancel();
			return;
		}
		if (draft.length === 0 && hasAnyMachine !== false) {
			setConfirmingEmpty(true);
			return;
		}
		submit();
	};

	const confirmLabel = mode === 'start' ? `Next: staff (${draft.length})` : `Save machines (${draft.length})`;

	return (
			<>
				<Modal open={open && !confirmingEmpty} onClose={onCancel} size="sm"
				       title={mode === 'start' ? 'Start process' : 'Machines'} subtitle={orderNumber}>
					<div className="machine-type-tabs">
						{TYPE_FILTERS.map((type) => (
								<button
										key={type}
										type="button"
										className={`machine-type-tab ${typeFilter === type ? 'machine-type-tab--active' : ''}`}
										onClick={() => changeType(type)}
								>
									{type === 'all' ? 'All' : MACHINE_TYPE_LABELS[type]}
								</button>
						))}
					</div>
					{loading && machines.length === 0 ? (
							<div className="center-box" style={{padding: '24px 0', minHeight: 0}}>
								<WashingMachine size={22} className="spin"/>
								<span>Loading machines…</span>
							</div>
					) : error ? (
							<div className="alert alert--error">{error}</div>
					) : machines.length > 0 ? (
							<>
								<div className="machine-option-list">
									{machines.map((machine) => {
										const disabled = busyElsewhere(machine);
										return (
												<label
														key={machine.id}
														className={`machine-option ${disabled ? 'machine-option--disabled' : ''}`}
												>
													<input
															type="checkbox"
															checked={draftIds.has(machine.id)}
															disabled={disabled}
															onChange={() => toggle(machine)}
													/>
													<span className="machine-option__name">{machine.name}</span>
													<span className="machine-option__meta">
														{disabled
																? `In use · ${machine.currentOrderNumber}`
																: MACHINE_TYPE_LABELS[machine.type]}
													</span>
												</label>
										);
									})}
								</div>
								{(hasNext || hasPrev) && (
										<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={onNext} onPrev={onPrev}
										            loading={loading}/>
								)}
							</>
					) : (
							<p className="muted" style={{fontSize: 13}}>
								{hasAnyMachine === false
										? 'No machines registered yet. The order can still be processed without one.'
										: 'No active machines of this type.'}
							</p>
					)}
					{draft.length > 0 && (
							<p className="machine-picker__selected">
								{draft.length} selected · {draft.map((m) => m.name).join(', ')}
							</p>
					)}
					<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 16}}>
						<Button type="button" variant="ghost" onClick={onCancel}>Cancel</Button>
						<Button type="button" disabled={submitting} onClick={confirm}>
							{submitting ? 'Saving…' : confirmLabel}
						</Button>
					</div>
				</Modal>

				<Modal open={open && confirmingEmpty} onClose={() => setConfirmingEmpty(false)} size="sm"
				       title="No machines selected">
					<p style={{fontSize: 13, marginTop: 0}}>
						{mode === 'start'
								? 'This order will be processed without any machine recorded. Continue?'
								: 'This removes every machine from the order. Continue?'}
					</p>
					<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 16}}>
						<Button type="button" variant="ghost" onClick={() => setConfirmingEmpty(false)}>Back</Button>
						<Button type="button" disabled={submitting} onClick={submit}>
							{submitting ? 'Saving…' : 'Continue without machines'}
						</Button>
					</div>
				</Modal>
			</>
	);
}
