/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {clearRefreshTokenCookie, setAccessToken} from './tokenStore';
import {emitSessionExpired} from './sessionEvents';

export function endSession(): void {
	setAccessToken(null);
	clearRefreshTokenCookie();
	emitSessionExpired();
}
