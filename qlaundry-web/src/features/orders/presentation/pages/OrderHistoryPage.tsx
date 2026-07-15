/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {Filter, MoreHorizontal, Package, PackagePlus, Search, X,} from 'lucide-react';
import {
	Badge,
	type BadgeTone,
	Button,
	Card,
	Drawer,
	Field,
	Input,
	Loading,
	PageHeader,
	Select,
	useToast
} from '@/core/ui';
import {formatCurrency, formatDate, formatRelative} from '@/core/utils/format';
import {useOrders} from '../useOrders';
import type {Order, OrderStatus} from '../../domain/Order';
import {CLOTHING_TYPE_LABELS, ORDER_STATUS_LABELS} from '../../domain/Order';

type FilterStatus = 'all' | OrderStatus;

function statusTone(status: OrderStatus): BadgeTone {
	if (status === 'completed') return 'success';
	if (['in_progress', 'picked_up', 'confirmed'].includes(status)) return 'info';
	if (status === 'ready' || status === 'out_for_delivery') return 'warning';
	if (status === 'cancelled') return 'neutral';
	return 'warning';
}

const NEXT_STATUS: Partial<Record<OrderStatus, OrderStatus>> = {
	pending: 'confirmed',
	confirmed: 'picked_up',
	picked_up: 'in_progress',
	in_progress: 'ready',
	ready: 'out_for_delivery',
	out_for_delivery: 'completed',
};

const NEXT_STATUS_LABEL: Partial<Record<OrderStatus, string>> = {
	pending: 'Konfirmasi',
	confirmed: 'Sudah diambil',
	picked_up: 'Mulai proses',
	in_progress: 'Tandai siap',
	ready: 'Kirim',
	out_for_delivery: 'Selesai',
};

export default function OrderHistoryPage() {
	const navigate = useNavigate();
	const {orders, loading, updateStatus, cancelOrder} = useOrders();
	const toast = useToast();

	const [search, setSearch] = useState('');
	const [filterStatus, setFilterStatus] = useState<FilterStatus>('all');
	const [filterPriority, setFilterPriority] = useState<'all' | 'normal' | 'express'>('all');
	const [dateFrom, setDateFrom] = useState('');
	const [dateTo, setDateTo] = useState('');
	const [showFilters, setShowFilters] = useState(false);
	const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

	if (loading) return <Loading label="Memuat order…"/>;

	const visible = orders.filter((o) => {
		const q = search.toLowerCase();
		const matchSearch =
				!q ||
				o.orderNumber.toLowerCase().includes(q) ||
				o.customerName.toLowerCase().includes(q) ||
				o.customerPhone.includes(q) ||
				o.serviceName.toLowerCase().includes(q);
		const matchStatus = filterStatus === 'all' || o.status === filterStatus;
		const matchPriority = filterPriority === 'all' || o.priority === filterPriority;
		const matchFrom = !dateFrom || o.pickupDate >= dateFrom;
		const matchTo = !dateTo || o.pickupDate <= dateTo;
		return matchSearch && matchStatus && matchPriority && matchFrom && matchTo;
	});

	const handleAdvance = async (order: Order) => {
		const next = NEXT_STATUS[order.status];
		if (!next) return;
		try {
			await updateStatus({orderId: order.id, status: next});
			toast.success(`Status order ${order.orderNumber} diperbarui: ${ORDER_STATUS_LABELS[next]}`);
			if (selectedOrder?.id === order.id) {
				setSelectedOrder((prev) => prev ? {...prev, status: next} : null);
			}
		} catch {
			toast.error('Gagal memperbarui status.');
		}
	};

	const handleCancel = async (order: Order) => {
		if (!confirm(`Batalkan order ${order.orderNumber}?`)) return;
		try {
			await cancelOrder(order.id, 'Dibatalkan oleh operator');
			toast.success(`Order ${order.orderNumber} dibatalkan.`);
			setSelectedOrder(null);
		} catch {
			toast.error('Gagal membatalkan order.');
		}
	};

	const clearFilters = () => {
		setSearch('');
		setFilterStatus('all');
		setFilterPriority('all');
		setDateFrom('');
		setDateTo('');
	};

	const hasActiveFilters =
			search || filterStatus !== 'all' || filterPriority !== 'all' || dateFrom || dateTo;

	return (
			<>
				<PageHeader
						title="Riwayat order"
						description={`${orders.length} total order · ${visible.length} ditampilkan`}
						actions={
							<div className="row" style={{gap: 8}}>
								<Button variant="ghost" onClick={() => setShowFilters((v) => !v)}>
									<Filter size={15}/> Filter {hasActiveFilters ? `(aktif)` : ''}
								</Button>
								<Button onClick={() => navigate('/orders/new')}>
									<PackagePlus size={15}/> Order baru
								</Button>
							</div>
						}
				/>

				{showFilters && (
						<Card style={{marginBottom: 20}}>
							<div className="filters-grid">
								<Field label="Cari">
									<div className="input-with-icon">
										<Search size={15}/>
										<Input
												value={search}
												onChange={(e) => setSearch(e.target.value)}
												placeholder="No. order, nama, telepon…"
										/>
									</div>
								</Field>
								<Field label="Status">
									<Select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value as FilterStatus)}>
										<option value="all">Semua status</option>
										{Object.entries(ORDER_STATUS_LABELS).map(([k, v]) => (
												<option key={k} value={k}>{v}</option>
										))}
									</Select>
								</Field>
								<Field label="Prioritas">
									<Select
											value={filterPriority}
											onChange={(e) => setFilterPriority(e.target.value as typeof filterPriority)}
									>
										<option value="all">Semua</option>
										<option value="normal">Normal</option>
										<option value="express">Express</option>
									</Select>
								</Field>
								<Field label="Dari tanggal">
									<Input type="date" value={dateFrom} onChange={(e) => setDateFrom(e.target.value)}/>
								</Field>
								<Field label="Sampai tanggal">
									<Input type="date" value={dateTo} onChange={(e) => setDateTo(e.target.value)}/>
								</Field>
								{hasActiveFilters && (
										<div style={{display: 'flex', alignItems: 'flex-end', paddingBottom: 16}}>
											<Button variant="ghost" onClick={clearFilters}>
												<X size={14}/> Reset filter
											</Button>
										</div>
								)}
							</div>
						</Card>
				)}

				<Card>
					<div className="table-wrap">
						<table className="table">
							<thead>
							<tr>
								<th>No. Order</th>
								<th>Pelanggan</th>
								<th>Layanan</th>
								<th>Jadwal</th>
								<th>Status</th>
								<th>Total</th>
								<th>Prioritas</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{visible.map((order) => (
									<tr key={order.id} className="table-row--clickable" onClick={() => setSelectedOrder(order)}>
										<td>
											<strong>{order.orderNumber}</strong>
											<span className="table-sub">{formatRelative(order.createdAt)}</span>
										</td>
										<td>
											<strong>{order.customerName}</strong>
											<span className="table-sub">{order.customerPhone}</span>
										</td>
										<td>{order.serviceName}</td>
										<td>
											<span>{formatDate(order.pickupDate)}</span>
											<span className="table-sub">→ {formatDate(order.estimatedDelivery)}</span>
										</td>
										<td>
											<Badge tone={statusTone(order.status)}>
												{ORDER_STATUS_LABELS[order.status]}
											</Badge>
										</td>
										<td>
											<strong>{formatCurrency(order.totalPrice)}</strong>
											{order.discount > 0 && (
													<span className="table-sub success-text">-{formatCurrency(order.discount)}</span>
											)}
										</td>
										<td>
											{order.priority === 'express' ? (
													<Badge tone="warning">⚡ Express</Badge>
											) : (
													<span className="muted" style={{fontSize: 12}}>Normal</span>
											)}
										</td>
										<td onClick={(e) => e.stopPropagation()}>
											<button
													className="icon-btn"
													aria-label="Detail"
													onClick={() => setSelectedOrder(order)}
											>
												<MoreHorizontal size={16}/>
											</button>
										</td>
									</tr>
							))}
							</tbody>
						</table>

						{!visible.length && (
								<div className="empty-state">
									<Package size={32}/>
									<strong>Tidak ada order yang cocok</strong>
									<span>Coba ubah filter atau kata kunci pencarian.</span>
									{hasActiveFilters && (
											<Button variant="ghost" onClick={clearFilters}>Reset filter</Button>
									)}
								</div>
						)}
					</div>
				</Card>

				<Drawer
						open={!!selectedOrder}
						onClose={() => setSelectedOrder(null)}
						title={selectedOrder ? `Order ${selectedOrder.orderNumber}` : ''}
						subtitle={selectedOrder ? ORDER_STATUS_LABELS[selectedOrder.status] : ''}
						width="520px"
						footer={
								selectedOrder && (
										<div className="row" style={{gap: 10, justifyContent: 'flex-end'}}>
											{!['completed', 'cancelled'].includes(selectedOrder.status) && (
													<>
														{NEXT_STATUS[selectedOrder.status] && (
																<Button onClick={() => void handleAdvance(selectedOrder)}>
																	{NEXT_STATUS_LABEL[selectedOrder.status]}
																</Button>
														)}
														<Button variant="danger" onClick={() => void handleCancel(selectedOrder)}>
															Batalkan
														</Button>
													</>
											)}
										</div>
								)
						}
				>
					{selectedOrder && (
							<div className="order-detail">
								<div className="detail-section">
									<h4>Informasi pelanggan</h4>
									<div className="detail-grid">
										<div><small>Nama</small><strong>{selectedOrder.customerName}</strong></div>
										<div><small>Telepon</small><strong>{selectedOrder.customerPhone}</strong></div>
										<div style={{gridColumn: '1/-1'}}>
											<small>Alamat</small><strong>{selectedOrder.customerAddress}</strong></div>
									</div>
								</div>

								<div className="detail-section">
									<h4>Detail layanan</h4>
									<div className="detail-grid">
										<div><small>Layanan</small><strong>{selectedOrder.serviceName}</strong></div>
										<div>
											<small>Prioritas</small>
											<strong>{selectedOrder.priority === 'express' ? '⚡ Express' : 'Normal'}</strong>
										</div>
										<div>
											<small>Pengambilan</small>
											<strong>{formatDate(selectedOrder.pickupDate)}</strong>
										</div>
										<div>
											<small>Estimasi selesai</small>
											<strong>{formatDate(selectedOrder.estimatedDelivery)}</strong>
										</div>
									</div>
								</div>

								<div className="detail-section">
									<h4>Pakaian</h4>
									<div className="items-list">
										{selectedOrder.items.map((item, idx) => (
												<div key={idx} className="item-row item-row--view">
													<span>{CLOTHING_TYPE_LABELS[item.type]}</span>
													<Badge tone="neutral">{item.quantity} pcs</Badge>
												</div>
										))}
									</div>
									{selectedOrder.weightKg && (
											<p className="muted" style={{marginTop: 8, fontSize: 13}}>
												Berat: <strong>{selectedOrder.weightKg} kg</strong>
											</p>
									)}
								</div>

								<div className="detail-section">
									<h4>Pembayaran</h4>
									<div className="detail-grid">
										<div>
											<small>Metode</small>
											<strong>
												{selectedOrder.paymentMethod === 'cash' ? 'Tunai' :
														selectedOrder.paymentMethod === 'transfer' ? 'Transfer' : 'QRIS'}
											</strong>
										</div>
										<div>
											<small>Status</small>
											<Badge tone={
												selectedOrder.paymentStatus === 'paid' ? 'success' :
														selectedOrder.paymentStatus === 'partial' ? 'warning' : 'neutral'
											}>
												{selectedOrder.paymentStatus === 'paid' ? 'Lunas' :
														selectedOrder.paymentStatus === 'partial' ? 'Cicilan' : 'Belum bayar'}
											</Badge>
										</div>
										<div><small>Subtotal</small><strong>{formatCurrency(selectedOrder.subtotal)}</strong></div>
										{selectedOrder.discount > 0 && (
												<div>
													<small>Diskon {selectedOrder.promoCode && `(${selectedOrder.promoCode})`}</small>
													<strong style={{color: 'var(--success)'}}>−{formatCurrency(selectedOrder.discount)}</strong>
												</div>
										)}
										<div style={{gridColumn: '1/-1'}}>
											<small>Total</small>
											<strong style={{fontSize: 18}}>{formatCurrency(selectedOrder.totalPrice)}</strong>
										</div>
									</div>
								</div>

								{selectedOrder.notes && (
										<div className="detail-section">
											<h4>Catatan pelanggan</h4>
											<p className="muted" style={{fontSize: 13}}>{selectedOrder.notes}</p>
										</div>
								)}
								{selectedOrder.staffNotes && (
										<div className="detail-section">
											<h4>Catatan staff</h4>
											<p className="muted" style={{fontSize: 13}}>{selectedOrder.staffNotes}</p>
										</div>
								)}

								<div className="detail-section">
									<div className="status-timeline">
										{(['pending', 'confirmed', 'picked_up', 'in_progress', 'ready', 'out_for_delivery', 'completed'] as OrderStatus[]).map((s) => {
											const statuses: OrderStatus[] = ['pending', 'confirmed', 'picked_up', 'in_progress', 'ready', 'out_for_delivery', 'completed'];
											const currentIdx = statuses.indexOf(selectedOrder.status);
											const thisIdx = statuses.indexOf(s);
											const done = thisIdx <= currentIdx && selectedOrder.status !== 'cancelled';
											return (
													<div key={s} className={`timeline-step ${done ? 'timeline-step--done' : ''}`}>
														<div className="timeline-dot"/>
														<span>{ORDER_STATUS_LABELS[s]}</span>
													</div>
											);
										})}
									</div>
								</div>
							</div>
					)}
				</Drawer>
			</>
	);
}
