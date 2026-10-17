
/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export interface TimeZoneGroup {
	offset: string;
	minutes: number;
	zones: string[];
}

const OFFSET_PATTERN = /^GMT(?:([+\-−])(\d{2}):(\d{2}))?$/;

function offsetOf(zone: string, at: Date): {offset: string; minutes: number} | null {
	const part = new Intl.DateTimeFormat('en-US', {timeZone: zone, timeZoneName: 'longOffset'})
			.formatToParts(at)
			.find((p) => p.type === 'timeZoneName');
	const match = part?.value.match(OFFSET_PATTERN);
	if (!match) return null;
	if (!match[1]) return {offset: '+00:00', minutes: 0};
	const sign = match[1] === '+' ? '+' : '-';
	const minutes = Number(match[2]) * 60 + Number(match[3]);
	return {offset: `${sign}${match[2]}:${match[3]}`, minutes: sign === '+' ? minutes : -minutes};
}

export function groupTimeZones(zones: string[], at: Date = new Date()): TimeZoneGroup[] {
	const groups = new Map<string, TimeZoneGroup>();
	for (const zone of new Set(zones)) {
		let resolved: {offset: string; minutes: number} | null;
		try {
			resolved = offsetOf(zone, at);
		} catch {
			resolved = null;
		}
		if (!resolved) continue;
		const group = groups.get(resolved.offset) ?? {offset: resolved.offset, minutes: resolved.minutes, zones: []};
		group.zones.push(zone);
		groups.set(resolved.offset, group);
	}
	return [...groups.values()]
			.map((group) => ({...group, zones: [...group.zones].sort()}))
			.sort((a, b) => a.minutes - b.minutes);
}

const PREFERRED_ZONES = ['UTC', 'Asia/Jakarta', 'Asia/Makassar', 'Asia/Jayapura'];

const LABEL_ZONE_COUNT = 3;

export function representativeZone(group: TimeZoneGroup): string {
	return PREFERRED_ZONES.find((zone) => group.zones.includes(zone)) ?? group.zones[0];
}

export function timeZoneGroupLabel(group: TimeZoneGroup): string {
	const lead = representativeZone(group);
	const names = [lead, ...group.zones.filter((zone) => zone !== lead)];
	const shown = names.slice(0, LABEL_ZONE_COUNT).join(', ');
	return `${group.offset} (${shown}${names.length > LABEL_ZONE_COUNT ? ', …' : ''})`;
}
