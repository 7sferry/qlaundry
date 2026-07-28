import {describe, expect, it} from 'vitest';
import {computeTier, TIER_THRESHOLDS} from './Customer';

describe('computeTier', () => {
	it('returns bronze for zero spend', () => {
		expect(computeTier(0)).toBe('bronze');
	});

	it('returns bronze below the silver threshold', () => {
		expect(computeTier(TIER_THRESHOLDS.silver - 1)).toBe('bronze');
	});

	it('returns silver at exactly the silver threshold', () => {
		expect(computeTier(TIER_THRESHOLDS.silver)).toBe('silver');
	});

	it('returns silver between the silver and gold thresholds', () => {
		expect(computeTier(1_000_000)).toBe('silver');
	});

	it('returns gold at exactly the gold threshold', () => {
		expect(computeTier(TIER_THRESHOLDS.gold)).toBe('gold');
	});

	it('returns gold between the gold and platinum thresholds', () => {
		expect(computeTier(3_000_000)).toBe('gold');
	});

	it('returns platinum at exactly the platinum threshold', () => {
		expect(computeTier(TIER_THRESHOLDS.platinum)).toBe('platinum');
	});

	it('returns platinum above the platinum threshold', () => {
		expect(computeTier(10_000_000)).toBe('platinum');
	});
});
