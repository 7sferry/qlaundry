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
	Truck,
	User2,
	WashingMachine,
	X,
	Zap,
} from 'lucide-react';
import {Badge, Button, Card, Field, Input, Modal, PageHeader, Pagination, Select, Textarea} from '@/core/ui';
import {formatCurrency} from '@/core/utils/format';
import {useOrders, useServices} from '../useOrders';
import {useCustomerSearch} from '@/features/customers/presentation/useCustomers';
import type {Customer} from '@/features/customers/domain/Customer';
import type {ClothingItem, ClothingType, PaymentMethod} from '../../domain/Order';
import {CLOTHING_TYPE_LABELS} from '../../domain/Order';

const CLOTHING_TYPES: ClothingType[] = [
	'shirt', 'pants', 'dress', 'jacket', 'bed_linen', 'towel', 'uniform', 'other',
];

const PAYMENT_METHODS: PaymentMethod[] = ['cash'];

function initials(fullName: string): string {
	return fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase();
}

export default function CreateOrderPage() {
	const navigate = useNavigate();
	const {placeOrder} = useOrders();
	const {services, loading: servicesLoading} = useServices();
	const {matches: customerMatches, searching: searchingCustomers, hasNext, hasPrev, search, goNext, goPrevious} =
			useCustomerSearch();

	const [selectedServiceId, setSelectedServiceId] = useState('');
	const [priority, setPriority] = useState<'normal' | 'express'>('normal');
	const [paymentMethod, setPaymentMethod] = useState<'cash' | 'transfer' | 'qris'>('cash');
	const [items, setItems] = useState<ClothingItem[]>([
		{type: 'shirt', label: 'Shirt / Blouse', quantity: 3},
	]);
	const [weightKg, setWeightKg] = useState<number | ''>('');
	const [submitting, setSubmitting] = useState(false);
	const [success, setSuccess] = useState(false);
	const [phoneSearch, setPhoneSearch] = useState('');
	const [nameSearch, setNameSearch] = useState('');
	const [searchModalOpen, setSearchModalOpen] = useState(false);
	const [selectedCustomerId, setSelectedCustomerId] = useState<string | undefined>(undefined);
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

	// Editing a customer field by hand after picking a match means the order no longer maps 1:1
	// to that customer record, so the link is dropped and the fields become a plain walk-in entry.
	const updateCustomerField = (key: 'customerName' | 'customerPhone' | 'customerAddress') => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
	) => {
		setSelectedCustomerId(undefined);
		setForm((prev) => ({...prev, [key]: e.target.value}));
	};

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

	const total = subtotal;

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

	// Phone takes priority when both fields are filled — it is the more specific lookup (exact match
	// on the blind index) while name is a prefix search likely to return more results.
	const searchCustomers = async () => {
		const phone = phoneSearch.trim();
		const name = nameSearch.trim();
		if (!phone && !name) return;
		setSearchModalOpen(true);
		await search(phone ? {phone} : {name});
	};

	const selectCustomer = (customer: Customer) => {
		setForm((prev) => ({
			...prev,
			customerName: customer.fullName,
			customerPhone: customer.phone,
			customerAddress: customer.address,
		}));
		setSelectedCustomerId(customer.id);
		setSearchModalOpen(false);
		setPhoneSearch('');
		setNameSearch('');
	};

	const clearSelectedCustomer = () => setSelectedCustomerId(undefined);

	const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		if (!service) return;
		setSubmitting(true);
		try {
			await placeOrder({
				customerId: selectedCustomerId,
				customerName: form.customerName,
				customerPhone: form.customerPhone,
				customerAddress: form.customerAddress,
				serviceId: service.id,
				items,
				quantity: totalQty,
				weightKg: weightKg === '' ? undefined : Number(weightKg),
				priority,
				paymentMethod,
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
              <span>Loading services…</span>
            </div>
    );
  }

	if (success) {
		return (
        <div className="center-box" style={{flexDirection: 'column', gap: 16, paddingTop: 80}}>
          <div className="success-circle"><Check size={32}/></div>
          <h2>Order created successfully!</h2>
          <p className="muted">Redirecting to order history…</p>
        </div>
    );
  }

	return (
			<>
    <PageHeader
            title="Create new order"
            description="Fill in the details below to schedule the laundry service."
        />

				<form onSubmit={submit}>
					<div className="order-layout">
						<div className="stack">
       <Card title="1. Choose service" subtitle="Select the required care type.">
								<div className="service-grid">
									{services.map((s) => (
											<button
													key={s.id}
													type="button"
													className={`service-card ${service?.id === s.id ? 'service-card--selected' : ''}`}
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
             {s.popular && <Badge tone="info">Popular</Badge>}
												</div>
												<strong>{s.name}</strong>
												<p>{s.description}</p>
												<div className="row row--between mt-16">
                      <span className="service-card__price">
                        {formatCurrency(s.pricePerUnit)}<small> / {s.unit}</small>
                      </span>
                          <span className="muted" style={{fontSize: 11}}>~{s.estimatedHours}h</span>
												</div>
											</button>
									))}
								</div>

								<div className="priority-row mt-16">
                <span className="field__label" style={{fontSize: 13, fontWeight: 600, color: 'var(--text-muted)'}}>
                  Work priority
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

       <Card title="2. Customer details" subtitle="Enter or look up the customer.">
								<div className="form-grid">
       <Field label="Find by phone number" htmlFor="phoneSearch">
										<div className="input-with-icon">
											<Phone size={16}/>
											<Input
												id="phoneSearch"
												type="tel"
												value={phoneSearch}
												onChange={(e) => setPhoneSearch(e.target.value)}
           placeholder="07XXXXXXXXX"
											/>
										</div>
									</Field>
       <Field label="Find by name" htmlFor="nameSearch">
										<div className="input-with-icon">
											<User2 size={16}/>
											<Input
												id="nameSearch"
												value={nameSearch}
												onChange={(e) => setNameSearch(e.target.value)}
           placeholder="Start typing a name…"
											/>
										</div>
									</Field>
								</div>

								<Button type="button" variant="ghost"
										disabled={searchingCustomers || (!phoneSearch.trim() && !nameSearch.trim())}
										onClick={() => void searchCustomers()} style={{marginBottom: 16}}>
                  <Search size={15}/> Search customer
                </Button>

								{selectedCustomerId && (
									<div className="customer-linked-chip">
										<Check size={13}/> Linked to an existing customer
										<button type="button" onClick={clearSelectedCustomer}>
											<X size={13}/> Clear
										</button>
									</div>
								)}

								<div className="form-grid">
         <Field label="Customer name" htmlFor="custName">
										<div className="input-with-icon">
											<User2 size={16}/>
											<Input
													id="custName"
													required
													value={form.customerName}
													onChange={updateCustomerField('customerName')}
             placeholder="Full name"
											/>
										</div>
									</Field>
         <Field label="Telephone" htmlFor="custPhone">
										<div className="input-with-icon">
											<Phone size={16}/>
											<Input
													id="custPhone"
													type="tel"
													required
													value={form.customerPhone}
													onChange={updateCustomerField('customerPhone')}
             placeholder="07XXXXXXXXX"
											/>
										</div>
									</Field>
								</div>
        <Field label="Pickup address" htmlFor="custAddr">
									<div className="input-with-icon">
										<MapPin size={16}/>
										<Input
												id="custAddr"
												required
												value={form.customerAddress}
												onChange={updateCustomerField('customerAddress')}
            placeholder="e.g. 10 High Street, City"
										/>
									</div>
								</Field>
							</Card>

       <Card title="3. Garment details" subtitle="Add garment types and quantities.">
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
                  <Plus size={14}/> Add garment type
                </Button>

        {service?.unit === 'kg' && (
                    <Field label="Estimated weight (kg)" htmlFor="weight"
                           hint="Optional — recalculated at weighing">
											<Input
													id="weight"
													type="number"
													step="0.1"
													min="0.1"
													value={weightKg}
													onChange={(e) => setWeightKg(e.target.value === '' ? '' : Number(e.target.value))}
             placeholder="e.g. 3.5"
													style={{maxWidth: 160}}
											/>
										</Field>
								)}

        <Field label="Special notes" htmlFor="notes" hint="Optional — instructions for the team">
									<Textarea
											id="notes"
											value={form.notes}
											onChange={update('notes')}
           placeholder="e.g. separate whites, no softener"
											rows={2}
									/>
								</Field>
							</Card>

       <Card title="4. Schedule & payment" subtitle="Choose the dates and payment method.">
								<div className="form-grid">
         <Field label="Pickup date" htmlFor="pickup">
										<Input
												id="pickup"
												type="date"
												required
												value={form.pickupDate}
												onChange={handlePickupChange}
												min={new Date().toISOString().split('T')[0]}
										/>
									</Field>
         <Field label="Estimated completion" htmlFor="delivery">
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

        <Field label="Payment method">
									<div className="payment-grid">
										{PAYMENT_METHODS.map((method) => (
												<button
														key={method}
														type="button"
														className={`payment-btn ${paymentMethod === method ? 'payment-btn--active' : ''}`}
														onClick={() => setPaymentMethod(method)}
												>
													{method === 'cash' && '💵'}
													{method === 'transfer' && '🏦'}
													{method === 'qris' && '📱'}
             <span>{method === 'cash' ? 'Cash' : method === 'transfer' ? 'Bank transfer' : 'QRIS'}</span>
												</button>
										))}
									</div>
								</Field>
							</Card>
						</div>

						<aside className="stack" style={{position: 'sticky', top: 80, alignSelf: 'start'}}>
       <Card title="Order summary">
								{service && (
										<div className="summary-service">
											<div className="order-icon"><WashingMachine size={18}/></div>
											<div>
												<strong>{service.name}</strong>
												<span>
                      {priority === 'express' ? '⚡ Express' : 'Normal'} · ~
                    {Math.round(service.estimatedHours * (priority === 'express' ? 0.6 : 1))}h
                    </span>
											</div>
										</div>
								)}

        <div className="summary-line">
                  <span>Unit price</span>
									<span>
                  {service ? formatCurrency(service.pricePerUnit * (priority === 'express' ? service.expressMultiplier : 1)) : '—'} / {service?.unit}
                </span>
								</div>
        <div className="summary-line">
                  <span>Quantity</span>
									<span>
                  {service?.unit === 'kg' && weightKg ? `${weightKg} kg` : `${totalQty} pcs`}
                </span>
								</div>
        <div className="summary-line">
                  <span>Subtotal</span>
									<span>{formatCurrency(subtotal)}</span>
								</div>
        <div className="summary-line">
                  <span>Pickup & delivery</span>
                  <span className="summary-free">Free</span>
                </div>

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
         {paymentMethod === 'cash' ? 'Pay cash at pickup' : paymentMethod === 'transfer' ? 'Transfer to outlet account' : 'Scan QRIS at outlet'}
                </div>

        <Button block type="submit" disabled={submitting || !form.customerName} style={{marginTop: 20}}>
                  {submitting ? 'Creating order…' : <><Check size={16}/> Create order <ArrowRight size={16}/></>}
                </Button>
							</Card>

       <Card className="help-card">
              <p className="muted" style={{fontSize: 13}}>Need help? Contact our team.</p>
              <a href="tel:+6281234567890"
                 style={{display: 'inline-flex', alignItems: 'center', gap: 6, marginTop: 8, fontSize: 14}}>
                <Phone size={14}/> 0812-3456-7890
              </a>
            </Card>
						</aside>
					</div>
				</form>

				<Modal open={searchModalOpen} onClose={() => setSearchModalOpen(false)} title="Select a customer" size="sm">
					{searchingCustomers ? (
							<div className="center-box" style={{padding: '24px 0', minHeight: 0}}>
								<WashingMachine size={22} className="spin"/>
								<span>Searching…</span>
							</div>
					) : customerMatches.length > 0 ? (
							<>
								<div className="customer-match-list">
									{customerMatches.map((c) => (
											<button key={c.id} type="button" className="customer-match" onClick={() => selectCustomer(c)}>
												<div className="customer-avatar">{initials(c.fullName)}</div>
												<div className="customer-match__info">
													<strong>{c.fullName}</strong>
													<span>{c.phone}{c.address ? ` · ${c.address}` : ''}</span>
												</div>
											</button>
									))}
								</div>
								{(hasNext || hasPrev) && (
										<Pagination
												hasNext={hasNext} hasPrev={hasPrev}
												onNext={() => void goNext()} onPrev={() => void goPrevious()}
												loading={searchingCustomers}
										/>
								)}
							</>
					) : (
							<p className="muted" style={{fontSize: 13}}>
								No matching customer found — you can still fill in the details manually for a walk-in order.
							</p>
					)}
				</Modal>
			</>
	);
}
