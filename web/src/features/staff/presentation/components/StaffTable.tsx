/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {Plus, Search, Trash2, Users} from 'lucide-react';
import {Button, Card, Field, Input} from '@/core/ui';
import {formatDate} from '@/core/utils/format';
import type {Staff} from '../../domain/Staff';

interface StaffTableProps {
	staff: Staff[];
	search: string;
	onSearchChange: (value: string) => void;
	onSelect: (staff: Staff) => void;
	onDelete: (staff: Staff) => void;
	onAdd: () => void;
	canDelete: (staff: Staff) => boolean;
}

export default function StaffTable({staff, search, onSearchChange, onSelect, onDelete, onAdd, canDelete}: StaffTableProps) {
	return (
			<Card style={{marginTop: 24, marginBottom: 20}}>
				<div className="filters">
					<Field>
						<div className="input-with-icon">
							<Search size={15}/>
							<Input
									value={search}
									onChange={(e) => onSearchChange(e.target.value)}
									placeholder="Cari nama…"
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
													        title="Hapus">
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
								<strong>Tidak ada staf</strong>
								<span>
              {search ? 'Coba ubah kata kunci pencarian.' : 'Tambahkan staf pertama Anda.'}
            </span>
								{!search && (
										<Button onClick={onAdd}><Plus size={14}/> Tambah staf</Button>
								)}
							</div>
					)}
				</div>
			</Card>
	);
}
