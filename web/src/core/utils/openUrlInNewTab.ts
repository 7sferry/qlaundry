/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

export class PopupBlockedError extends Error {
	constructor() {
		super('Your browser blocked the new tab. Allow pop-ups for this site and try again.');
		this.name = 'PopupBlockedError';
	}
}

export async function openUrlInNewTab(resolveUrl: () => Promise<string>): Promise<void> {
	const tab = window.open('', '_blank');
	if (tab) tab.opener = null;

	try {
		const url = await resolveUrl();

		const opened = tab ?? window.open(url, '_blank');
		if (!opened) throw new PopupBlockedError();
		if (tab) tab.location.href = url;
	} catch (error) {
		tab?.close();
		throw error;
	}
}
