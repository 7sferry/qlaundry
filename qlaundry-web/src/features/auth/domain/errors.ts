/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

/**
 * The reset token is expired, consumed, or missing — the flow cannot continue
 * and must be restarted from the beginning. Pages route back to /login on this.
 */
export class ResetSessionExpiredError extends Error {
	constructor(message = 'Sesi reset password sudah tidak berlaku. Silakan ulangi dari awal.') {
		super(message);
		this.name = 'ResetSessionExpiredError';
	}
}
