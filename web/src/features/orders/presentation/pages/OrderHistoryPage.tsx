/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useEffect, useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {FileText, Filter, MoreHorizontal, Package, PackagePlus, Search, X,} from 'lucide-react';
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
	Pagination,
	Select,
	useToast
} from '@/core/ui';
import type {SortBy, SortDirection} from '@/core/pagination/Pagination';
import {formatCurrency, formatDate, formatRelative} from '@/core/utils/format';
import {PopupBlockedError} from '@/core/utils/openUrlInNewTab';
import {useOrders} from '../useOrders';
import type {Order, OrderStatus} from '../../domain/Order';
import {CLOTHING_TYPE_LABELS, ORDER_STATUS_LABELS} from '../../domain/Order';

const SORT_OPTIONS: { value: string; sortBy: SortBy; sortDir: SortDirection; label: string }[] = [
	{value: 'id-desc', sortBy: 'id', sortDir: 'desc', label: 'Newest first'},
	{value: 'id-asc', sortBy: 'id', sortDir: 'asc', label: 'Oldest first'},
	{value: 'name-asc', sortBy: 'name', sortDir: 'asc', label: 'Customer A→Z'},
	{value: 'name-desc', sortBy: 'name', sortDir: 'desc', label: 'Customer Z→A'},
];

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
	pending: 'Confirm',
	confirmed: 'Picked up',
	picked_up: 'Start process',
	in_progress: 'Mark ready',
	ready: 'Dispatch',
	out_for_delivery: 'Complete',
};

export default function OrderHistoryPage() {
	const navigate = useNavigate();
	const {
		orders, loading, hasNext, hasPrev, refresh, goNext, goPrevious, updateStatus, cancelOrder, viewInvoice,
	} = useOrders();
	const toast = useToast();

	const [search, setSearch] = useState('');
	const [openingInvoice, setOpeningInvoice] = useState(false);
	const [filterStatus, setFilterStatus] = useState<FilterStatus>('all');
	const [filterPriority, setFilterPriority] = useState<'all' | 'normal' | 'express'>('all');
	const [dateFrom, setDateFrom] = useState('');
	const [dateTo, setDateTo] = useState('');
	const [sortBy, setSortBy] = useState<SortBy>('id');
	const [sortDir, setSortDir] = useState<SortDirection>('desc');
	const [showFilters, setShowFilters] = useState(false);
	const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

	const didMount = useRef(false);
	useEffect(() => {
		if (!didMount.current) {
			didMount.current = true;
			return;
		}
		const timer = setTimeout(() => {
			void refresh({
				search: search || undefined,
				status: filterStatus === 'all' ? undefined : filterStatus,
				priority: filterPriority === 'all' ? undefined : filterPriority,
				from: dateFrom || undefined,
				to: dateTo || undefined,
				sortBy,
				sortDir,
			});
		}, 300);
		return () => clearTimeout(timer);
	}, [search, filterStatus, filterPriority, dateFrom, dateTo, sortBy, sortDir, refresh]);

	const handleSortChange = useCallback((by: SortBy, dir: SortDirection) => {
		setSortBy(by);
		setSortDir(dir);
	}, []);

	if (loading && orders.length === 0) return <Loading label="Loading orders…"/>;

	const handleAdvance = async (order: Order) => {
		const next = NEXT_STATUS[order.status];
		if (!next) return;
		try {
			await updateStatus({orderId: order.id, status: next});
			toast.success(`Order ${order.orderNumber} status updated: ${ORDER_STATUS_LABELS[next]}`);
			if (selectedOrder?.id === order.id) {
				setSelectedOrder((prev) => prev ? {...prev, status: next} : null);
			}
		} catch {
			toast.error('Failed to update status.');
		}
	};

	const handleViewInvoice = async (order: Order) => {
		setOpeningInvoice(true);
		try {
			await viewInvoice(order);
		} catch (error) {
			toast.error(error instanceof PopupBlockedError ? error.message : 'Failed to generate invoice PDF.');
		} finally {
			setOpeningInvoice(false);
		}
	};

	const handleCancel = async (order: Order) => {
		if (!confirm(`Cancel order ${order.orderNumber}?`)) return;
		try {
			await cancelOrder(order.id, 'Cancelled by operator');
			toast.success(`Order ${order.orderNumber} cancelled.`);
			setSelectedOrder(null);
		} catch {
			toast.error('Failed to cancel order.');
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
						title="Order history"
						description={`${orders.length} orders on this page`}
						actions={
							<div className="row" style={{gap: 8}}>
 							<Button variant="ghost" onClick={() => setShowFilters((v) => !v)}>
 								<Filter size={15}/> Filters {hasActiveFilters ? `(active)` : ''}
 							</Button>
 							<Button onClick={() => navigate('/orders/new')}>
 								<PackagePlus size={15}/> New order
 							</Button>
							</div>
						}
				/>

				{showFilters && (
						<Card style={{marginBottom: 20}}>
							<div className="filters-grid">
								<Field label="Search">
									<div className="input-with-icon">
										<Search size={15}/>
										<Input
												value={search}
												onChange={(e) => setSearch(e.target.value)}
												placeholder="Order no., name, phone…"
										/>
									</div>
								</Field>
								<Field label="Status">
									<Select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value as FilterStatus)}>
										<option value="all">All statuses</option>
										{Object.entries(ORDER_STATUS_LABELS).map(([k, v]) => (
											<option key={k} value={k}>{v}</option>
										))}
									</Select>
								</Field>
								<Field label="Priority">
									<Select
											value={filterPriority}
											onChange={(e) => setFilterPriority(e.target.value as typeof filterPriority)}
									>
										<option value="all">All</option>
										<option value="normal">Normal</option>
										<option value="express">Express</option>
									</Select>
								</Field>
								<Field label="Created from" hint="Filters by order creation date">
									<Input type="date" value={dateFrom} onChange={(e) => setDateFrom(e.target.value)}/>
								</Field>
								<Field label="Created to" hint="Filters by order creation date">
									<Input type="date" value={dateTo} onChange={(e) => setDateTo(e.target.value)}/>
								</Field>
								<Field label="Sort by">
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
								{hasActiveFilters && (
									<div style={{display: 'flex', alignItems: 'flex-end', paddingBottom: 16}}>
										<Button variant="ghost" onClick={clearFilters}>
											<X size={14}/> Reset filters
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
								<th>Order no.</th>
								<th>Customer</th>
								<th>Service</th>
								<th>Schedule</th>
								<th>Status</th>
								<th>Total</th>
								<th>Priority</th>
								<th/>
							</tr>
							</thead>
							<tbody>
							{orders.map((order) => (
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

						{!orders.length && (
								<div className="empty-state">
									<Package size={32}/>
									<strong>No matching orders</strong>
									<span>Try changing filters or the search term.</span>
									{hasActiveFilters && (
											<Button variant="ghost" onClick={clearFilters}>Reset filters</Button>
										)}
								</div>
						)}
					</div>

					<Pagination hasNext={hasNext} hasPrev={hasPrev} onNext={() => void goNext()} onPrev={() => void goPrevious()}
					            loading={loading}/>
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
											<Button
													variant="ghost"
													disabled={openingInvoice}
													onClick={() => void handleViewInvoice(selectedOrder)}
											>
												<FileText size={15}/> {openingInvoice ? 'Generating…' : 'View invoice'}
											</Button>
											{!['completed', 'cancelled'].includes(selectedOrder.status) && (
													<>
														{NEXT_STATUS[selectedOrder.status] && (
       									<Button onClick={() => void handleAdvance(selectedOrder)}>
       										{NEXT_STATUS_LABEL[selectedOrder.status]}
       									</Button>
														)}
     									<Button variant="danger" onClick={() => void handleCancel(selectedOrder)}>
     										Cancel
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
  							<h4>Customer information</h4>
									<div className="detail-grid">
  								<div><small>Name</small><strong>{selectedOrder.customerName}</strong></div>
  								<div><small>Telephone</small><strong>{selectedOrder.customerPhone}</strong></div>
										<div style={{gridColumn: '1/-1'}}>
  									<small>Address</small><strong>{selectedOrder.customerAddress}</strong></div>
									</div>
								</div>

								<div className="detail-section">
  							<h4>Service details</h4>
									<div className="detail-grid">
  								<div><small>Service</small><strong>{selectedOrder.serviceName}</strong></div>
										<div>
  									<small>Priority</small>
  									<strong>{selectedOrder.priority === 'express' ? '⚡ Express' : 'Normal'}</strong>
										</div>
										<div>
  									<small>Pickup</small>
											<strong>{formatDate(selectedOrder.pickupDate)}</strong>
										</div>
										<div>
  									<small>Estimated completion</small>
											<strong>{formatDate(selectedOrder.estimatedDelivery)}</strong>
										</div>
									</div>
								</div>

								<div className="detail-section">
  							<h4>Garments</h4>
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
  										Weight: <strong>{selectedOrder.weightKg} kg</strong>
  									</p>
									)}
								</div>

								<div className="detail-section">
  							<h4>Payment</h4>
									<div className="detail-grid">
										<div>
   								<small>Method</small>
											<strong>
   									{selectedOrder.paymentMethod === 'cash' ? 'Cash' :
   											selectedOrder.paymentMethod === 'transfer' ? 'Bank transfer' : 'QRIS'}
											</strong>
										</div>
										<div>
   								<small>Status</small>
											<Badge tone={
												selectedOrder.paymentStatus === 'paid' ? 'success' :
														selectedOrder.paymentStatus === 'partial' ? 'warning' : 'neutral'
											}>
   									{selectedOrder.paymentStatus === 'paid' ? 'Paid' :
   											selectedOrder.paymentStatus === 'partial' ? 'Partial' : 'Unpaid'}
											</Badge>
										</div>
   							<div><small>Subtotal</small><strong>{formatCurrency(selectedOrder.subtotal)}</strong></div>
										{selectedOrder.discount > 0 && (
												<div>
    									<small>Discount</small>
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
  									<h4>Customer notes</h4>
  									<p className="muted" style={{fontSize: 13}}>{selectedOrder.notes}</p>
  								</div>
  						)}
  						{selectedOrder.staffNotes && (
  								<div className="detail-section">
  									<h4>Staff notes</h4>
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
