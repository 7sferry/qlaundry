/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useCallback, useMemo, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {
	ArrowRight,
	Check,
	MapPin,
	Minus,
	Phone,
	Plus,
	Search,
	Sparkles,
	Tag,
	Truck,
	User2,
	WashingMachine,
	Zap,
} from 'lucide-react';
import {Badge, Button, Card, Field, Input, PageHeader, Select, Textarea} from '@/core/ui';
import {formatCurrency} from '@/core/utils/format';
import {useOrders, useServices} from '../useOrders';
import {useCustomers} from '@/features/customers/presentation/useCustomers';
import type {ClothingItem, ClothingType} from '../../domain/Order';
import {CLOTHING_TYPE_LABELS} from '../../domain/Order';

const CLOTHING_TYPES: ClothingType[] = [
	'shirt', 'pants', 'dress', 'jacket', 'bed_linen', 'towel', 'uniform', 'other',
];

const PROMO_CODES: Record<string, number> = {
	NEWUSER: 10,
	LOYAL10: 10,
	HEMAT15: 15,
	MEMBER20: 20,
};

export default function CreateOrderPage() {
	const navigate = useNavigate();
	const {placeOrder} = useOrders();
	const {services, loading: servicesLoading} = useServices();
	const {findByPhone} = useCustomers();

	const [selectedServiceId, setSelectedServiceId] = useState('wash-fold');
	const [priority, setPriority] = useState<'normal' | 'express'>('normal');
	const [paymentMethod, setPaymentMethod] = useState<'cash' | 'transfer' | 'qris'>('cash');
	const [promoCode, setPromoCode] = useState('');
	const [promoApplied, setPromoApplied] = useState<{ code: string; pct: number } | null>(null);
	const [promoError, setPromoError] = useState('');
	const [items, setItems] = useState<ClothingItem[]>([
		{type: 'shirt', label: 'Shirt / Blouse', quantity: 3},
	]);
	const [weightKg, setWeightKg] = useState<number | ''>('');
	const [submitting, setSubmitting] = useState(false);
	const [success, setSuccess] = useState(false);
	const [phoneSearch, setPhoneSearch] = useState('');
	const [form, setForm] = useState({
		customerName: '',
		customerPhone: '',
		customerAddress: '',
		pickupDate: new Date().toISOString().split('T')[0],
		estimatedDelivery: '',
		notes: '',
	});

	const update = (key: keyof typeof form) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const service = useMemo(
			() => services.find((s) => s.id === selectedServiceId) ?? services[0],
			[services, selectedServiceId],
	);

	const totalQty = useMemo(
			() => items.reduce((sum, i) => sum + i.quantity, 0),
			[items],
	);

	const subtotal = useMemo(() => {
		if (!service) return 0;
		const qty = service.unit === 'kg' ? Number(weightKg) || totalQty : totalQty;
		const multiplier = priority === 'express' ? service.expressMultiplier : 1;
		return Math.round(service.pricePerUnit * multiplier * qty);
	}, [service, weightKg, totalQty, priority]);

	const discount = useMemo(
			() => (promoApplied ? Math.round(subtotal * (promoApplied.pct / 100)) : 0),
			[subtotal, promoApplied],
	);

	const total = subtotal - discount;

	const autoDelivery = useCallback(
			(pickup: string) => {
				if (!service || !pickup) return '';
				const hours = service.estimatedHours * (priority === 'express' ? 0.6 : 1);
				const d = new Date(pickup);
				d.setHours(d.getHours() + Math.round(hours));
				return d.toISOString().split('T')[0];
			},
			[service, priority],
	);

	const handlePickupChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const val = e.target.value;
		setForm((prev) => ({
			...prev,
			pickupDate: val,
			estimatedDelivery: autoDelivery(val),
		}));
	};

	const addItem = () =>
			setItems((prev) => [...prev, {type: 'shirt', label: CLOTHING_TYPE_LABELS.shirt, quantity: 1}]);

	const removeItem = (idx: number) =>
			setItems((prev) => prev.filter((_, i) => i !== idx));

	const updateItem = (idx: number, field: keyof ClothingItem, value: string | number) =>
			setItems((prev) =>
					prev.map((item, i) => {
						if (i !== idx) return item;
						if (field === 'type') {
							const t = value as ClothingType;
							return {...item, type: t, label: CLOTHING_TYPE_LABELS[t]};
						}
						return {...item, [field]: value};
					}),
			);

	const lookupPhone = async () => {
		if (!phoneSearch) return;
		const customer = await findByPhone(phoneSearch);
		if (customer) {
			setForm((prev) => ({
				...prev,
				customerName: customer.fullName,
				customerPhone: customer.phone,
				customerAddress: customer.address,
			}));
		}
	};

	const applyPromo = () => {
		setPromoError('');
		const pct = PROMO_CODES[promoCode.toUpperCase()];
		if (!pct) {
			setPromoError('Kode promo tidak valid atau sudah kedaluwarsa.');
			return;
		}
		setPromoApplied({code: promoCode.toUpperCase(), pct});
	};

	const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		if (!service) return;
		setSubmitting(true);
		try {
			await placeOrder({
				customerName: form.customerName,
				customerPhone: form.customerPhone,
				customerAddress: form.customerAddress,
				serviceId: service.id,
				items,
				quantity: totalQty,
				weightKg: weightKg === '' ? undefined : Number(weightKg),
				priority,
				paymentMethod,
				promoCode: promoApplied?.code,
				pickupDate: form.pickupDate,
				estimatedDelivery: form.estimatedDelivery || autoDelivery(form.pickupDate),
				notes: form.notes,
			});
			setSuccess(true);
			setTimeout(() => navigate('/orders/history'), 1200);
		} catch {
			setSubmitting(false);
		}
	};

	if (servicesLoading) {
		return (
				<div className="center-box">
					<WashingMachine size={28} className="spin"/>
					<span>Memuat layanan…</span>
				</div>
		);
	}

	if (success) {
		return (
				<div className="center-box" style={{flexDirection: 'column', gap: 16, paddingTop: 80}}>
					<div className="success-circle"><Check size={32}/></div>
					<h2>Order berhasil dibuat!</h2>
					<p className="muted">Mengalihkan ke riwayat order…</p>
				</div>
		);
	}

	return (
			<>
				<PageHeader
						title="Buat order baru"
						description="Isi detail di bawah untuk menjadwalkan layanan laundry."
				/>

				<form onSubmit={submit}>
					<div className="order-layout">
						<div className="stack">
							<Card title="1. Pilih layanan" subtitle="Pilih jenis perawatan yang dibutuhkan.">
								<div className="service-grid">
									{services.map((s) => (
											<button
													key={s.id}
													type="button"
													className={`service-card ${selectedServiceId === s.id ? 'service-card--selected' : ''}`}
													onClick={() => {
														setSelectedServiceId(s.id);
														setForm((prev) => ({
															...prev,
															estimatedDelivery: autoDelivery(prev.pickupDate),
														}));
													}}
											>
												<div className="service-card__top">
													<span className="service-card__icon"><Sparkles size={16}/></span>
													{s.popular && <Badge tone="info">Populer</Badge>}
												</div>
												<strong>{s.name}</strong>
												<p>{s.description}</p>
												<div className="row row--between mt-16">
                      <span className="service-card__price">
                        {formatCurrency(s.pricePerUnit)}<small> / {s.unit}</small>
                      </span>
													<span className="muted" style={{fontSize: 11}}>~{s.estimatedHours}j</span>
												</div>
											</button>
									))}
								</div>

								<div className="priority-row mt-16">
                <span className="field__label" style={{fontSize: 13, fontWeight: 600, color: 'var(--text-muted)'}}>
                  Prioritas pengerjaan
                </span>
									<div className="priority-toggle">
										<button
												type="button"
												className={`priority-btn ${priority === 'normal' ? 'priority-btn--active' : ''}`}
												onClick={() => setPriority('normal')}
										>
											<WashingMachine size={14}/> Normal
										</button>
										<button
												type="button"
												className={`priority-btn priority-btn--express ${priority === 'express' ? 'priority-btn--active priority-btn--express-active' : ''}`}
												onClick={() => setPriority('express')}
										>
											<Zap size={14}/> Express
											{service && (
													<Badge tone="warning">×{service.expressMultiplier}</Badge>
											)}
										</button>
									</div>
								</div>
							</Card>

							<Card title="2. Data pelanggan" subtitle="Masukkan atau cari data pelanggan.">
								<div className="phone-search">
									<Field label="Cari pelanggan via nomor HP" htmlFor="phoneSearch">
										<div className="input-with-icon">
											<Phone size={16}/>
											<Input
													id="phoneSearch"
													type="tel"
													value={phoneSearch}
													onChange={(e) => setPhoneSearch(e.target.value)}
													placeholder="08xxxxxxxxxx"
											/>
										</div>
									</Field>
									<Button type="button" variant="ghost" onClick={() => void lookupPhone()}>
										<Search size={15}/> Cari
									</Button>
								</div>

								<div className="form-grid">
									<Field label="Nama pelanggan" htmlFor="custName">
										<div className="input-with-icon">
											<User2 size={16}/>
											<Input
													id="custName"
													required
													value={form.customerName}
													onChange={update('customerName')}
													placeholder="Nama lengkap"
											/>
										</div>
									</Field>
									<Field label="Nomor telepon" htmlFor="custPhone">
										<div className="input-with-icon">
											<Phone size={16}/>
											<Input
													id="custPhone"
													type="tel"
													required
													value={form.customerPhone}
													onChange={update('customerPhone')}
													placeholder="08xxxxxxxxxx"
											/>
										</div>
									</Field>
								</div>
								<Field label="Alamat pengambilan" htmlFor="custAddr">
									<div className="input-with-icon">
										<MapPin size={16}/>
										<Input
												id="custAddr"
												required
												value={form.customerAddress}
												onChange={update('customerAddress')}
												placeholder="Jl. contoh No. 1, Kota"
										/>
									</div>
								</Field>
							</Card>

							<Card title="3. Detail pakaian" subtitle="Tambahkan jenis dan jumlah pakaian.">
								<div className="items-list">
									{items.map((item, idx) => (
											<div key={idx} className="item-row">
												<Select
														value={item.type}
														onChange={(e) => updateItem(idx, 'type', e.target.value)}
														style={{flex: 1}}
												>
													{CLOTHING_TYPES.map((t) => (
															<option key={t} value={t}>{CLOTHING_TYPE_LABELS[t]}</option>
													))}
												</Select>
												<div className="quantity-control" style={{width: 140}}>
													<button
															type="button"
															onClick={() => updateItem(idx, 'quantity', Math.max(1, item.quantity - 1))}
													>
														<Minus size={14}/>
													</button>
													<Input
															type="number"
															min={1}
															max={99}
															value={item.quantity}
															onChange={(e) => updateItem(idx, 'quantity', Number(e.target.value))}
													/>
													<button
															type="button"
															onClick={() => updateItem(idx, 'quantity', Math.min(99, item.quantity + 1))}
													>
														<Plus size={14}/>
													</button>
												</div>
												{items.length > 1 && (
														<button type="button" className="icon-btn icon-btn--danger" onClick={() => removeItem(idx)}>
															×
														</button>
												)}
											</div>
									))}
								</div>
								<Button type="button" variant="ghost" onClick={addItem} style={{marginTop: 10}}>
									<Plus size={14}/> Tambah jenis pakaian
								</Button>

								{service?.unit === 'kg' && (
										<Field label="Estimasi berat (kg)" htmlFor="weight"
										       hint="Opsional — akan dihitung ulang saat timbang">
											<Input
													id="weight"
													type="number"
													step="0.1"
													min="0.1"
													value={weightKg}
													onChange={(e) => setWeightKg(e.target.value === '' ? '' : Number(e.target.value))}
													placeholder="cth. 3.5"
													style={{maxWidth: 160}}
											/>
										</Field>
								)}

								<Field label="Catatan khusus" htmlFor="notes" hint="Opsional — instruksi untuk tim">
									<Textarea
											id="notes"
											value={form.notes}
											onChange={update('notes')}
											placeholder="cth. pisahkan pakaian putih, jangan gunakan pewangi"
											rows={2}
									/>
								</Field>
							</Card>

							<Card title="4. Jadwal & pembayaran" subtitle="Tentukan tanggal dan metode pembayaran.">
								<div className="form-grid">
									<Field label="Tanggal pengambilan" htmlFor="pickup">
										<Input
												id="pickup"
												type="date"
												required
												value={form.pickupDate}
												onChange={handlePickupChange}
												min={new Date().toISOString().split('T')[0]}
										/>
									</Field>
									<Field label="Estimasi selesai" htmlFor="delivery">
										<Input
												id="delivery"
												type="date"
												required
												value={form.estimatedDelivery || autoDelivery(form.pickupDate)}
												onChange={update('estimatedDelivery')}
												min={form.pickupDate}
										/>
									</Field>
								</div>

								<Field label="Metode pembayaran">
									<div className="payment-grid">
										{(['cash', 'transfer', 'qris'] as const).map((method) => (
												<button
														key={method}
														type="button"
														className={`payment-btn ${paymentMethod === method ? 'payment-btn--active' : ''}`}
														onClick={() => setPaymentMethod(method)}
												>
													{method === 'cash' && '💵'}
													{method === 'transfer' && '🏦'}
													{method === 'qris' && '📱'}
													<span>{method === 'cash' ? 'Tunai' : method === 'transfer' ? 'Transfer' : 'QRIS'}</span>
												</button>
										))}
									</div>
								</Field>
							</Card>
						</div>

						<aside className="stack" style={{position: 'sticky', top: 80, alignSelf: 'start'}}>
							<Card title="Ringkasan order">
								{service && (
										<div className="summary-service">
											<div className="order-icon"><WashingMachine size={18}/></div>
											<div>
												<strong>{service.name}</strong>
												<span>
                      {priority === 'express' ? '⚡ Express' : 'Normal'} · ~
													{Math.round(service.estimatedHours * (priority === 'express' ? 0.6 : 1))}j
                    </span>
											</div>
										</div>
								)}

								<div className="summary-line">
									<span>Harga satuan</span>
									<span>
                  {service ? formatCurrency(service.pricePerUnit * (priority === 'express' ? service.expressMultiplier : 1)) : '—'} / {service?.unit}
                </span>
								</div>
								<div className="summary-line">
									<span>Jumlah</span>
									<span>
                  {service?.unit === 'kg' && weightKg ? `${weightKg} kg` : `${totalQty} pcs`}
                </span>
								</div>
								<div className="summary-line">
									<span>Subtotal</span>
									<span>{formatCurrency(subtotal)}</span>
								</div>
								<div className="summary-line">
									<span>Antar-jemput</span>
									<span className="summary-free">Gratis</span>
								</div>

								<div className="promo-row">
									<Field>
										<div className="input-with-icon">
											<Tag size={14}/>
											<Input
													value={promoCode}
													onChange={(e) => {
														setPromoCode(e.target.value);
														setPromoApplied(null);
														setPromoError('');
													}}
													placeholder="Kode promo"
													disabled={!!promoApplied}
											/>
										</div>
									</Field>
									<Button
											type="button"
											variant="ghost"
											onClick={applyPromo}
											disabled={!promoCode || !!promoApplied}
									>
										Pakai
									</Button>
								</div>
								{promoApplied && (
										<div className="promo-success">
											<Check size={13}/> Diskon {promoApplied.pct}% diterapkan
										</div>
								)}
								{promoError && <p style={{color: 'var(--danger)', fontSize: 12, marginTop: 4}}>{promoError}</p>}

								{discount > 0 && (
										<div className="summary-line" style={{color: 'var(--success)'}}>
											<span>Diskon ({promoApplied?.pct}%)</span>
											<span>−{formatCurrency(discount)}</span>
										</div>
								)}

								<div className="summary-total">
									<span>Total</span>
									<span>{formatCurrency(total)}</span>
								</div>

								<div style={{
									marginTop: 6,
									color: 'var(--text-muted)',
									fontSize: 12,
									display: 'flex',
									alignItems: 'center',
									gap: 6
								}}>
									<Truck size={13}/>
									{paymentMethod === 'cash' ? 'Bayar tunai saat pengambilan' : paymentMethod === 'transfer' ? 'Transfer ke rekening outlet' : 'Scan QRIS di outlet'}
								</div>

								<Button block type="submit" disabled={submitting || !form.customerName} style={{marginTop: 20}}>
									{submitting ? 'Membuat order…' : <><Check size={16}/> Buat order <ArrowRight size={16}/></>}
								</Button>
							</Card>

							<Card className="help-card">
								<p className="muted" style={{fontSize: 13}}>Butuh bantuan? Hubungi tim kami.</p>
								<a href="tel:+6281234567890"
								   style={{display: 'inline-flex', alignItems: 'center', gap: 6, marginTop: 8, fontSize: 14}}>
									<Phone size={14}/> 0812-3456-7890
								</a>
							</Card>
						</aside>
					</div>
				</form>
			</>
	);
}
