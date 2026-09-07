/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

export type SortDirection = 'asc' | 'desc';
export type PageDirection = 'next' | 'prev';

/**
 * `SortBy` is deliberately not a single cross-feature union here — each feature's own repository
 * declares its own `XxxSortBy` (e.g. `PromotionSortBy`, `OrderSortBy`) matching exactly what its
 * backend's sort-by enum actually supports, and passes it as this type parameter. A shared union
 * papers over the fact that "sort by name" means a different backend enum member per service
 * (`order-service`'s is `CUSTOMER_NAME`, not `NAME` — see CLAUDE.md) — pretending it's one shared
 * concept is exactly what caused that mismatch to go unnoticed.
 */
export interface PaginationParams<SortBy extends string = string> {
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
