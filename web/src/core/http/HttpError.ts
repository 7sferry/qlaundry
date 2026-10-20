/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export class HttpError extends Error {
	readonly status: number;
	readonly problem?: Record<string, unknown>;

	constructor(status: number, message: string, problem?: Record<string, unknown>) {
		super(message);
		this.name = 'HttpError';
		this.status = status;
		this.problem = problem;
	}
}
