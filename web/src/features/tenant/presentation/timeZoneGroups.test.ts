
/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {describe, expect, it} from 'vitest';
import {groupTimeZones} from './timeZoneGroups';

const WINTER = new Date('2026-01-15T12:00:00Z');

describe('groupTimeZones', () => {
	it('groups zones that share an offset and sorts their names', () => {
		const groups = groupTimeZones(['Asia/Jakarta', 'Asia/Bangkok', 'Asia/Ho_Chi_Minh'], WINTER);
		expect(groups).toEqual([
			{offset: '+07:00', minutes: 420, zones: ['Asia/Bangkok', 'Asia/Ho_Chi_Minh', 'Asia/Jakarta']},
		]);
	});

	it('orders groups from the most negative offset to the most positive', () => {
		const groups = groupTimeZones(['Asia/Jakarta', 'UTC', 'America/New_York', 'Asia/Kolkata'], WINTER);
		expect(groups.map((g) => g.offset)).toEqual(['-05:00', '+00:00', '+05:30', '+07:00']);
	});

	it('reads the offset in effect at the given date', () => {
		const summer = new Date('2026-07-15T12:00:00Z');
		expect(groupTimeZones(['America/New_York'], summer)[0].offset).toBe('-04:00');
		expect(groupTimeZones(['America/New_York'], WINTER)[0].offset).toBe('-05:00');
	});

	it('skips names the runtime does not know and collapses repeats', () => {
		const groups = groupTimeZones(['Asia/Jakarta', 'Asia/Jakarta', 'Not/AZone'], WINTER);
		expect(groups).toHaveLength(1);
		expect(groups[0].zones).toEqual(['Asia/Jakarta']);
	});
});
