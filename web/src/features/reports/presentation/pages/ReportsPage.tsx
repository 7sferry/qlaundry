/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useState} from 'react';
import {BarChart2, Download, TrendingUp, Users, WashingMachine,} from 'lucide-react';
import {
	Bar,
	BarChart,
	CartesianGrid,
	ComposedChart,
	Line,
	Pie,
	PieChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts';
import {Button, Card, Loading, PageHeader, StatCard} from '@/core/ui';
import {formatCurrency} from '@/core/utils/format';
import {useDashboard} from '@/features/dashboard/presentation/useDashboard';
import {useCustomers} from '@/features/customers/presentation/useCustomers';

type Period = 'week' | 'month' | 'quarter' | 'year';

const COLORS = ['var(--brand)', 'var(--info)', 'var(--success)', 'var(--warning)', 'var(--danger)'];

export default function ReportsPage() {
	const {stats, loading} = useDashboard();
	const {customers} = useCustomers();
	const [period, setPeriod] = useState<Period>('month');

	if (loading && !stats) return <Loading label="Preparing reports…"/>;

	const topCustomers = [...customers]
			.sort((a, b) => b.totalSpend - a.totalSpend)
			.slice(0, 5);

	return (
			<>
				<PageHeader
						title="Reports & analytics"
						description="Monitor overall performance of your laundry business."
						actions={
							<div className="row" style={{gap: 10}}>
								<div className="period-tabs">
 								{(['week', 'month', 'quarter', 'year'] as Period[]).map((p) => (
 										<button
 												key={p}
 												className={`period-tab ${period === p ? 'period-tab--active' : ''}`}
 												onClick={() => setPeriod(p)}
 										>
 											{p === 'week' ? 'Week' : p === 'month' ? 'Month' : p === 'quarter' ? 'Quarter' : 'Year'}
 										</button>
 								))}
								</div>
  						<Button variant="ghost">
  							<Download size={15}/> Export
  						</Button>
							</div>
						}
				/>

				<div className="grid grid--stats">
					<StatCard
							icon={<BarChart2 size={20}/>}
							value={stats?.monthOrders ?? 0}
							label="Total orders"
							hint={`This month · ${stats?.ordersGrowth ?? 0}% vs last month`}
					/>
					<StatCard
							icon={<TrendingUp size={20}/>}
							value={formatCurrency(stats?.monthRevenue ?? 0)}
							label="Total revenue"
							hint={`${stats?.revenueGrowth ?? 0}% vs last month`}
					/>
					<StatCard
							icon={<WashingMachine size={20}/>}
							value={stats?.inProgressOrders ?? 0}
							label="In progress"
							hint="Active orders"
					/>
					<StatCard
							icon={<Users size={20}/>}
							value={customers.length}
							label="Total customers"
							hint="Registered"
					/>
				</div>

				<div className="grid grid--2 mt-24">
					<Card
							title="Revenue trend"
							subtitle="Monthly revenue for the last 6 months"
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
											yAxisId="left"
											axisLine={false}
											tickLine={false}
											tick={{fill: 'var(--text-muted)', fontSize: 11}}
											tickFormatter={(v: number) => `${(v / 1000000).toFixed(1)}jt`}
									/>
									<YAxis
											yAxisId="right"
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
									<Bar yAxisId="left" dataKey="revenue" fill="var(--brand)" radius={[5, 5, 0, 0]} barSize={28}
									     opacity={0.9}/>
									<Line
											yAxisId="right"
											type="monotone"
											dataKey="orders"
											stroke="var(--warning)"
											strokeWidth={2.5}
											dot={{r: 4, fill: 'var(--warning)', strokeWidth: 2, stroke: 'var(--surface)'}}
									/>
								</ComposedChart>
							</ResponsiveContainer>
						</div>
					</Card>

						<Card title="Service distribution" subtitle="Contribution by service this month">
						<div style={{display: 'flex', gap: 20, alignItems: 'center'}}>
							<div style={{flex: '0 0 160px', height: 160}}>
								<ResponsiveContainer width="100%" height="100%">
									<PieChart>
										<Pie
												data={(stats?.serviceBreakdown ?? []).map((item, idx) => ({
													...item,
													fill: COLORS[idx % COLORS.length],
												}))}
												dataKey="count"
												nameKey="serviceName"
												cx="50%"
												cy="50%"
												innerRadius={45}
												outerRadius={70}
												paddingAngle={3}
										/>
										<Tooltip
												contentStyle={{
													background: 'var(--surface)',
													border: '1px solid var(--border)',
													borderRadius: 8,
													fontSize: 12,
												}}
										/>
									</PieChart>
								</ResponsiveContainer>
							</div>
							<div className="service-mix" style={{flex: 1}}>
								{(stats?.serviceBreakdown ?? []).map((s, idx) => (
										<div key={s.serviceId} className="mix-row">
											<div className="mix-label">
												<span className="mix-dot" style={{background: COLORS[idx % COLORS.length]}}/>
												<span style={{fontSize: 13}}>{s.serviceName}</span>
											</div>
											<div style={{textAlign: 'right'}}>
												<strong style={{display: 'block', fontSize: 13}}>{s.count}x</strong>
												<span className="muted" style={{fontSize: 11}}>{s.percentage}%</span>
											</div>
										</div>
								))}
							</div>
						</div>
					</Card>
				</div>

				<div className="grid grid--2 mt-24">
						<Card title="Revenue by service" subtitle="Total revenue per service">
						<div style={{height: 220}}>
							<ResponsiveContainer width="100%" height="100%">
								<BarChart
										layout="vertical"
										data={[...(stats?.serviceBreakdown ?? [])].sort((a, b) => b.revenue - a.revenue)}
										margin={{left: 10, right: 20}}
								>
									<CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="var(--border)"/>
									<XAxis
											type="number"
											axisLine={false}
											tickLine={false}
											tick={{fill: 'var(--text-muted)', fontSize: 11}}
											tickFormatter={(v: number) => `${(v / 1000000).toFixed(1)}jt`}
									/>
									<YAxis
											type="category"
											dataKey="serviceName"
											axisLine={false}
											tickLine={false}
											tick={{fill: 'var(--text-muted)', fontSize: 12}}
											width={110}
									/>
									<Tooltip
											contentStyle={{
												background: 'var(--surface)',
												border: '1px solid var(--border)',
												borderRadius: 8,
												fontSize: 12,
											}}
  									formatter={(v) => [formatCurrency(v as number), 'Revenue']}
									/>
									<Bar dataKey="revenue" fill="var(--brand)" radius={[0, 5, 5, 0]} barSize={18}/>
								</BarChart>
							</ResponsiveContainer>
						</div>
					</Card>

						<Card title="Top customers" subtitle="Top 5 customers by spend">
						<div className="top-customers">
							{topCustomers.map((c, idx) => (
									<div key={c.id} className="top-customer-row">
										<span className="top-rank">{idx + 1}</span>
										<div className="top-customer-info">
											<strong>{c.fullName}</strong>
  									<span className="muted" style={{fontSize: 12}}>{c.totalOrders} orders</span>
										</div>
										<div style={{textAlign: 'right'}}>
											<strong style={{display: 'block', fontSize: 14}}>{formatCurrency(c.totalSpend)}</strong>
										</div>
									</div>
							))}
						{!topCustomers.length && (
								<div className="empty-state" style={{padding: 24}}>
									<Users size={24}/>
									<span>No customer data yet</span>
								</div>
						)}
						</div>
					</Card>
				</div>

				<div className="mt-24">
						<Card title="Order status summary" subtitle="Distribution of all orders by status">
						<div style={{height: 60, display: 'flex', gap: 4, borderRadius: 8, overflow: 'hidden'}}>
							{(stats?.statusDistribution ?? []).filter(s => s.count > 0).map((s, idx) => {
								const total = (stats?.statusDistribution ?? []).reduce((sum, x) => sum + x.count, 0);
								const pct = total > 0 ? (s.count / total) * 100 : 0;
								return (
										<div
												key={s.status}
												style={{
													width: `${pct}%`,
													background: COLORS[idx % COLORS.length],
													display: 'flex',
													alignItems: 'center',
													justifyContent: 'center',
													fontSize: 11,
													fontWeight: 700,
													color: '#fff',
													minWidth: pct > 5 ? undefined : 0,
													overflow: 'hidden',
													transition: 'width 0.4s ease',
												}}
												title={`${s.label}: ${s.count}`}
										>
											{pct > 8 && s.count}
										</div>
								);
							})}
						</div>
						<div className="status-dist" style={{marginTop: 16}}>
							{(stats?.statusDistribution ?? []).filter(s => s.count > 0).map((s, idx) => (
									<div key={s.status} className="status-dist__row">
										<div className="row" style={{gap: 10}}>
											<span style={{
												width: 10,
												height: 10,
												borderRadius: 3,
												background: COLORS[idx % COLORS.length],
												flexShrink: 0,
												display: 'inline-block'
											}}/>
											<span style={{fontSize: 13}}>{s.label}</span>
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
