/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useNavigate} from 'react-router-dom';
import {
	ArrowDownRight,
	ArrowUpRight,
	Clock3,
	Package,
	PackageCheck,
	Plus,
	RefreshCw,
	Truck,
	WashingMachine,
} from 'lucide-react';
import {Bar, CartesianGrid, ComposedChart, Line, ResponsiveContainer, Tooltip, XAxis, YAxis,} from 'recharts';
import type {BadgeTone} from '@/core/ui';
import {Badge, Button, Card, Loading, PageHeader, StatCard} from '@/core/ui';
import {formatCurrency, formatTime} from '@/core/utils/format';
import {useDashboard} from '../useDashboard';

function statusTone(status: string): BadgeTone {
	if (status === 'completed') return 'success';
	if (['in_progress', 'picked_up', 'confirmed'].includes(status)) return 'info';
	if (status === 'ready' || status === 'out_for_delivery') return 'warning';
	if (status === 'cancelled') return 'neutral';
	return 'warning';
}

function statusLabel(status: string): string {
  const map: Record<string, string> = {
    pending: 'Pending',
    confirmed: 'Confirmed',
    picked_up: 'Picked Up',
    in_progress: 'In Progress',
    ready: 'Ready',
    out_for_delivery: 'Out for Delivery',
    completed: 'Completed',
    cancelled: 'Cancelled',
  };
  return map[status] ?? status;
}

export default function DashboardPage() {
	const navigate = useNavigate();
	const {stats, loading, refresh} = useDashboard();

	if (loading && !stats) return <Loading label="Loading dashboard…"/>;

	const growth = stats?.revenueGrowth ?? 0;
	const ordersGrowth = stats?.ordersGrowth ?? 0;

	return (
			<>
				<PageHeader
						title="Welcome back 👋"
						description="Here’s today’s summary of your laundry operations."
						actions={
							<div className="row" style={{gap: 10}}>
								<Button variant="ghost" onClick={() => void refresh()}>
									<RefreshCw size={15}/> Refresh
								</Button>
 							<Button onClick={() => navigate('/orders/new')}>
 								<Plus size={15}/> New order
 							</Button>
							</div>
						}
				/>

				<div className="grid grid--stats">
					<StatCard
							icon={<Package size={20}/>}
							value={stats?.todayOrders ?? 0}
 						label="Orders today"
							hint={
								<span className={`trend ${ordersGrowth >= 0 ? 'trend--up' : 'trend--down'}`}>
				      {ordersGrowth >= 0 ? <ArrowUpRight size={12}/> : <ArrowDownRight size={12}/>}
								{Math.abs(ordersGrowth)}% vs yesterday
				    </span>
							}
					/>
					<StatCard
							icon={<PackageCheck size={20}/>}
							value={formatCurrency(stats?.todayRevenue ?? 0)}
 						label="Revenue today"
							hint={
								<span className={`trend ${growth >= 0 ? 'trend--up' : 'trend--down'}`}>
				      {growth >= 0 ? <ArrowUpRight size={12}/> : <ArrowDownRight size={12}/>}
								{Math.abs(growth)}% vs yesterday
				    </span>
							}
					/>
					<StatCard
							icon={<Clock3 size={20}/>}
							value={stats?.inProgressOrders ?? 0}
 						label="In progress"
 						hint="Active orders now"
					/>
					<StatCard
							icon={<WashingMachine size={20}/>}
							value={stats?.pendingOrders ?? 0}
 						label="Awaiting confirmation"
 						hint="Needs attention"
					/>
				</div>

				<div className="grid grid--2 mt-24">
					<Card
							title="Revenue trend"
							subtitle="Revenue & order count for the last 6 months"
							actions={
								<Badge tone="info">{stats?.monthOrders ?? 0} orders this month</Badge>
							}
					>
						<div className="chart-wrap">
							<ResponsiveContainer width="100%" height="100%">
								<ComposedChart data={stats?.revenueTrend ?? []}>
									<CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)"/>
									<XAxis
											dataKey="period"
											axisLine={false}
											tickLine={false}
											tick={{fill: 'var(--text-muted)', fontSize: 12}}
									/>
									<YAxis
											yAxisId="revenue"
											orientation="left"
											axisLine={false}
											tickLine={false}
											tick={{fill: 'var(--text-muted)', fontSize: 11}}
											tickFormatter={(v: number) => `${(v / 1000000).toFixed(1)}jt`}
									/>
									<YAxis
											yAxisId="orders"
											orientation="right"
											axisLine={false}
											tickLine={false}
											tick={{fill: 'var(--text-muted)', fontSize: 11}}
									/>
									<Tooltip
											contentStyle={{
												background: 'var(--surface)',
												border: '1px solid var(--border)',
												borderRadius: 10,
												fontSize: 13,
											}}
   								formatter={(value, name) =>
   										name === 'revenue' ? [formatCurrency(value as number), 'Revenue'] : [value as number, 'Orders']
   								}
									/>
									<Bar yAxisId="revenue" dataKey="revenue" fill="var(--brand)" radius={[5, 5, 0, 0]} barSize={28}
									     opacity={0.85}/>
									<Line yAxisId="orders" type="monotone" dataKey="orders" stroke="var(--warning)" strokeWidth={2}
									      dot={{r: 3, fill: 'var(--warning)'}}/>
								</ComposedChart>
							</ResponsiveContainer>
						</div>
					</Card>

						<Card title="Today’s schedule" subtitle="Scheduled pickups & deliveries">
						{stats?.todaySchedule?.length ? (
								<div className="schedule-list">
									{stats.todaySchedule.map((item) => (
											<div key={item.orderId} className="schedule-row">
												<div className={`schedule-type schedule-type--${item.type}`}>
													{item.type === 'pickup' ? <Package size={14}/> : <Truck size={14}/>}
												</div>
												<div className="schedule-row__info">
													<strong>{item.customerName}</strong>
    									<span>
									{item.orderNumber} · {item.type === 'pickup' ? 'Pickup' : 'Delivery'}
									</span>
												</div>
												<div className="schedule-row__right">
													<Badge tone={statusTone(item.status)}>{statusLabel(item.status)}</Badge>
													<small className="muted">{formatTime(item.scheduledAt)}</small>
												</div>
											</div>
									))}
								</div>
						) : (
 							<div className="empty-state">
 								<Clock3 size={28}/>
 								<strong>No schedule today</strong>
 								<span>Add a new order to get started.</span>
 							</div>
						)}
					</Card>
				</div>

				<div className="grid grid--2 mt-24">
					<Card title="Service distribution" subtitle="This month’s service breakdown">
						<div className="service-breakdown">
							{(stats?.serviceBreakdown ?? []).map((s) => (
									<div key={s.serviceId} className="breakdown-row">
										<div className="breakdown-row__label">
											<WashingMachine size={14}/>
											<span>{s.serviceName}</span>
										</div>
										<div className="breakdown-row__bar">
											<div className="breakdown-bar" style={{width: `${s.percentage}%`}}/>
										</div>
										<div className="breakdown-row__stats">
											<span className="breakdown-count">{s.count}x</span>
											<strong>{formatCurrency(s.revenue)}</strong>
										</div>
									</div>
							))}
						</div>
					</Card>

					<Card title="Order status" subtitle="Current status distribution">
						<div className="status-dist">
							{(stats?.statusDistribution ?? []).map((s) => (
									<div key={s.status} className="status-dist__row">
										<div className="row" style={{gap: 10}}>
											<span className={`status-dot status-dot--${s.status}`}/>
											<span>{s.label}</span>
										</div>
										<strong>{s.count}</strong>
									</div>
							))}
						</div>
					</Card>
				</div>
			</>
	);
}
