/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {AtSign, Trash2} from 'lucide-react';
import {Badge, Button, Drawer} from '@/core/ui';
import {formatDate} from '@/core/utils/format';
import type {Staff} from '../../domain/Staff';

interface StaffDetailDrawerProps {
	staff: Staff | null;
	open: boolean;
	onClose: () => void;
	onDelete: (staff: Staff) => void;
}

export default function StaffDetailDrawer({staff, open, onClose, onDelete}: StaffDetailDrawerProps) {
	return (
			<Drawer
					open={open}
					onClose={onClose}
					title={staff?.fullName}
					subtitle={staff ? `Bergabung ${formatDate(staff.joinedAt)}` : ''}
					width="460px"
					footer={
							staff && (
									<div className="row" style={{gap: 8, justifyContent: 'flex-end'}}>
										<Button variant="danger" onClick={() => onDelete(staff)}>
											<Trash2 size={14}/> Hapus
										</Button>
									</div>
							)
					}
			>
				{staff && (
						<div className="customer-detail">
							<div className="customer-detail__hero">
								<div className="customer-avatar customer-avatar--lg">
									{staff.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()}
								</div>
								<div>
									<h3>{staff.fullName}</h3>
									<Badge tone="info"><AtSign size={11}/> {staff.username}</Badge>
								</div>
							</div>

							<div className="detail-section">
								<h4>Kontak</h4>
								<div className="detail-grid">
									<div>
										<small>Telepon</small>
										<strong>{staff.phones.length ? staff.phones.join(', ') : '—'}</strong>
									</div>
									<div>
										<small>Email</small>
										<strong>{staff.emails.length ? staff.emails.join(', ') : '—'}</strong>
									</div>
									<div style={{gridColumn: '1/-1'}}>
										<small>Alamat</small>
										<strong>{staff.addresses.length ? staff.addresses.join('; ') : '—'}</strong>
									</div>
								</div>
							</div>

							{staff.description && (
									<div className="detail-section">
										<h4>Deskripsi</h4>
										<p className="muted" style={{fontSize: 13}}>{staff.description}</p>
									</div>
							)}

							<div className="detail-section">
								<h4>Aktivitas</h4>
								<div className="detail-grid">
									<div>
										<small>Bergabung</small>
										<strong>{formatDate(staff.joinedAt)}</strong>
									</div>
								</div>
							</div>
						</div>
				)}
			</Drawer>
	);
}
