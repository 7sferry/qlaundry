/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {Plus, Search, Trash2, Users} from 'lucide-react';
import {Button, Card, Field, Input, Pagination, Select} from '@/core/ui';
import {formatDate} from '@/core/utils/format';
import type {SortBy, SortDirection} from '@/core/pagination/Pagination';
import type {Staff} from '../../domain/Staff';

interface StaffTableProps {
	staff: Staff[];
	search: string;
	onSearchChange: (value: string) => void;
	sortBy: SortBy;
	sortDir: SortDirection;
	onSortChange: (sortBy: SortBy, sortDir: SortDirection) => void;
	onSelect: (staff: Staff) => void;
	onDelete: (staff: Staff) => void;
	onAdd: () => void;
	canDelete: (staff: Staff) => boolean;
	hasNext: boolean;
	hasPrev: boolean;
	onNext: () => void;
	onPrev: () => void;
	loading: boolean;
}

const SORT_OPTIONS: { value: string; sortBy: SortBy; sortDir: SortDirection; label: string }[] = [
	{value: 'id-desc', sortBy: 'id', sortDir: 'desc', label: 'Newest first'},
	{value: 'id-asc', sortBy: 'id', sortDir: 'asc', label: 'Oldest first'},
	{value: 'name-asc', sortBy: 'name', sortDir: 'asc', label: 'Name A→Z'},
	{value: 'name-desc', sortBy: 'name', sortDir: 'desc', label: 'Name Z→A'},
];

export default function StaffTable({
	                                    staff, search, onSearchChange, sortBy, sortDir, onSortChange, onSelect, onDelete,
	                                    onAdd, canDelete, hasNext, hasPrev, onNext, onPrev, loading,
                                    }: StaffTableProps) {
	return (
			<Card style={{marginTop: 24, marginBottom: 20}}>
				<div className="filters">
					<Field>
						<div className="input-with-icon">
							<Search size={15}/>
							<Input
									value={search}
									onChange={(e) => onSearchChange(e.target.value)}
  							placeholder="Search name…"
							/>
						</div>
					</Field>
					<Field>
						<Select
								value={`${sortBy}-${sortDir}`}
								onChange={(e) => {
									const opt = SORT_OPTIONS.find((o) => o.value === e.target.value);
									if (opt) onSortChange(opt.sortBy, opt.sortDir);
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
 						<th>Staff</th>
 						<th>Contact</th>
 						<th>Address</th>
 						<th>Joined</th>
							<th/>
						</tr>
						</thead>
						<tbody>
						{staff.map((s) => (
								<tr
										key={s.id}
										className="table-row--clickable"
										onClick={() => onSelect(s)}
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
											{canDelete(s) && (
   										<button className="icon-btn icon-btn--danger" onClick={() => onDelete(s)}
									        title="Delete">
														<Trash2 size={14}/>
													</button>
											)}
										</div>
									</td>
								</tr>
						))}
						</tbody>
					</table>

					{!staff.length && (
 						<div className="empty-state">
 							<Users size={28}/>
 							<strong>No staff</strong>
 							<span>
 							{search ? 'Try changing the search term.' : 'Add your first staff member.'}
 							</span>
 							{!search && (
 									<Button onClick={onAdd}><Plus size={14}/> Add staff</Button>
 							)}
 						</div>
					)}
				</div>

				<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={onNext} onPrev={onPrev} loading={loading}/>
			</Card>
	);
}
