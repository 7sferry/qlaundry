import {afterEach, describe, expect, it, vi} from 'vitest';
import {
	compactCurrencyUnit,
	formatCompactCurrency,
	formatCurrency,
	formatDate,
	formatDatetime,
	formatPhone,
	formatRelative,
	formatTime,
} from './format';

describe('compactCurrencyUnit', () => {
	it('uses no suffix below one thousand', () => {
		expect(compactCurrencyUnit(750)).toEqual({divisor: 1, suffix: ''});
	});

	it('uses rb when the max is below one million', () => {
		expect(compactCurrencyUnit(999_999).suffix).toBe('rb');
		expect(compactCurrencyUnit(1_000).suffix).toBe('rb');
	});

	it('uses jt when the max is below one billion', () => {
		expect(compactCurrencyUnit(1_000_000).suffix).toBe('jt');
		expect(compactCurrencyUnit(999_999_999).suffix).toBe('jt');
	});

	it('uses mlr from one billion and T from one trillion', () => {
		expect(compactCurrencyUnit(1_000_000_000).suffix).toBe('mlr');
		expect(compactCurrencyUnit(2_500_000_000_000).suffix).toBe('T');
	});
});

describe('formatCompactCurrency', () => {
	it('scales every tick by the chart unit', () => {
		const unit = compactCurrencyUnit(850_000);
		expect(formatCompactCurrency(0, unit)).toBe('0rb');
		expect(formatCompactCurrency(250_000, unit)).toBe('250rb');
		expect(formatCompactCurrency(850_000, unit)).toBe('850rb');
	});

	it('keeps one decimal in the id-ID style', () => {
		const unit = compactCurrencyUnit(3_400_000);
		expect(formatCompactCurrency(1_500_000, unit)).toBe('1,5jt');
		expect(formatCompactCurrency(3_000_000, unit)).toBe('3jt');
	});
});

describe('formatCurrency', () => {
	it('formats zero as IDR', () => {
		const result = formatCurrency(0);
		expect(result).toMatch(/Rp/);
		expect(result.replace(/\D/g, '')).toBe('0');
	});

	it('formats a positive amount with correct digits', () => {
		const result = formatCurrency(1_500_000);
		expect(result).toMatch(/Rp/);
		expect(result.replace(/\D/g, '')).toBe('1500000');
	});

	it('returns a non-empty string for large amounts', () => {
		expect(formatCurrency(50_000_000)).toBeTruthy();
	});
});

describe('formatDate', () => {
	it('formats an ISO date string and includes the year', () => {
		expect(formatDate('2024-07-14')).toMatch(/2024/);
	});

	it('formats a Date object', () => {
		expect(formatDate(new Date('2024-01-15'))).toMatch(/2024/);
	});

	it('returns the raw value for an invalid date string', () => {
		expect(formatDate('not-a-date')).toBe('not-a-date');
	});

	it('produces a non-empty string for a valid date', () => {
		const result = formatDate('2024-12-31');
		expect(result).toBeTruthy();
		expect(result).not.toBe('2024-12-31');
	});
});

describe('formatTime', () => {
	it('formats a valid ISO datetime string', () => {
		const result = formatTime('2024-07-14T09:30:00');
		expect(result).toMatch(/\d+[.:]\d+/);
	});

	it('returns an empty string for an invalid date', () => {
		expect(formatTime('invalid')).toBe('');
	});

	it('formats a Date object', () => {
		const result = formatTime(new Date('2024-07-14T09:30:00'));
		expect(result).toMatch(/\d+/);
	});
});

describe('formatDatetime', () => {
	it('combines date and time with a comma separator', () => {
		const result = formatDatetime('2024-07-14T09:30:00');
		expect(result).toContain(',');
		expect(result).toMatch(/2024/);
	});
});

describe('formatRelative', () => {
	const NOW = new Date('2024-07-14T12:00:00.000Z').getTime();

	afterEach(() => {
		vi.restoreAllMocks();
	});

	it('returns "Baru saja" for times less than 1 minute ago', () => {
		vi.spyOn(Date, 'now').mockReturnValue(NOW);
		expect(formatRelative(new Date(NOW - 30_000))).toBe('Baru saja');
	});

	it('returns minutes for 1–59 minutes ago', () => {
		vi.spyOn(Date, 'now').mockReturnValue(NOW);
		expect(formatRelative(new Date(NOW - 30 * 60_000))).toBe('30 menit lalu');
	});

	it('returns hours for 1–23 hours ago', () => {
		vi.spyOn(Date, 'now').mockReturnValue(NOW);
		expect(formatRelative(new Date(NOW - 3 * 60 * 60_000))).toBe('3 jam lalu');
	});

	it('returns days for 1–6 days ago', () => {
		vi.spyOn(Date, 'now').mockReturnValue(NOW);
		expect(formatRelative(new Date(NOW - 3 * 24 * 60 * 60_000))).toBe('3 hari lalu');
	});

	it('falls back to a formatted date string for dates older than 7 days', () => {
		vi.spyOn(Date, 'now').mockReturnValue(NOW);
		const result = formatRelative(new Date(NOW - 14 * 24 * 60 * 60_000));
		expect(result).not.toMatch(/lalu/);
		expect(result).not.toBe('Baru saja');
	});

	it('accepts an ISO string input', () => {
		vi.spyOn(Date, 'now').mockReturnValue(NOW);
		expect(formatRelative(new Date(NOW - 10_000).toISOString())).toBe('Baru saja');
	});
});

describe('formatPhone', () => {
	it('converts 08xx format to +628xx', () => {
		expect(formatPhone('08123456789')).toBe('+628123456789');
	});

	it('adds + prefix to 62xx format', () => {
		expect(formatPhone('628123456789')).toBe('+628123456789');
	});

	it('normalises an already-international number', () => {
		expect(formatPhone('+628123456789')).toBe('+628123456789');
	});

	it('strips separators before normalising', () => {
		expect(formatPhone('0812-3456-789')).toBe('+628123456789');
	});

	it('returns unrecognised formats unchanged', () => {
		expect(formatPhone('1234567890')).toBe('1234567890');
	});
});
