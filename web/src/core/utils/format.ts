/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

const idrFormatter = new Intl.NumberFormat('id-ID', {
	style: 'currency',
	currency: 'IDR',
	minimumFractionDigits: 0,
	maximumFractionDigits: 0,
});

export const formatCurrency = (value: number): string => idrFormatter.format(value);

export interface CompactCurrencyUnit {
	divisor: number;
	suffix: string;
}

const COMPACT_CURRENCY_UNITS: CompactCurrencyUnit[] = [
	{divisor: 1_000_000_000_000, suffix: 'T'},
	{divisor: 1_000_000_000, suffix: 'mlr'},
	{divisor: 1_000_000, suffix: 'jt'},
	{divisor: 1_000, suffix: 'rb'},
];

export const compactCurrencyUnit = (max: number): CompactCurrencyUnit =>
		COMPACT_CURRENCY_UNITS.find((unit) => max >= unit.divisor) ?? {divisor: 1, suffix: ''};

export const formatCompactCurrency = (value: number, unit: CompactCurrencyUnit): string =>
		`${(value / unit.divisor).toLocaleString('id-ID', {maximumFractionDigits: 1})}${unit.suffix}`;

export const formatDate =(value: string | Date): string => {
	const date = typeof value === 'string' ? new Date(value) : value;
	if (Number.isNaN(date.getTime())) return String(value);
	return date.toLocaleDateString('en-GB', {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
	});
};

export const formatTime = (value: string | Date): string => {
	const date = typeof value === 'string' ? new Date(value) : value;
	if (Number.isNaN(date.getTime())) return '';
	return date.toLocaleTimeString('en-GB', {hour: '2-digit', minute: '2-digit'});
};

export const formatDatetime = (value: string | Date): string =>
		`${formatDate(value)}, ${formatTime(value)}`;

export const formatRelative = (value: string | Date): string => {
	const date = typeof value === 'string' ? new Date(value) : value;
	const diffMs = Date.now() - date.getTime();
	const diffMin = Math.floor(diffMs / 60000);
	if (diffMin < 1) return 'Just now';
	if (diffMin < 60) return `${diffMin} minute${diffMin === 1 ? '' : 's'} ago`;
	const diffHr = Math.floor(diffMin / 60);
	if (diffHr < 24) return `${diffHr} hour${diffHr === 1 ? '' : 's'} ago`;
	const diffDay = Math.floor(diffHr / 24);
	if (diffDay < 7) return `${diffDay} day${diffDay === 1 ? '' : 's'} ago`;
	return formatDate(date);
};

export const formatPhone = (phone: string): string => {
	const digits = phone.replace(/\D/g, '');
	if (digits.startsWith('62')) return '+' + digits;
	if (digits.startsWith('0')) return '+62' + digits.slice(1);
	return phone;
};
