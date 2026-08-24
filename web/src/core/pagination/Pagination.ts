/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

export type SortBy = 'id' | 'name';
export type SortDirection = 'asc' | 'desc';
export type PageDirection = 'next' | 'prev';

export interface PaginationParams {
	cursor?: string;
	direction?: PageDirection;
	sortBy?: SortBy;
	sortDir?: SortDirection;
}

export interface Page<T> {
	items: T[];
	nextCursor: string | null;
	prevCursor: string | null;
}
