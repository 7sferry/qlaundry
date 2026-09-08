/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

export type SortDirection = 'asc' | 'desc';

/** Mirrors the backend's `PaginationConstant` — keep both in sync. */
export const DEFAULT_PAGE_SIZE = 3;
export const PAGE_SIZE_OPTIONS = [3, 5, 10, 20, 50] as const;

/**
 * `SortBy` is deliberately not a single cross-feature union here — each feature's own repository
 * declares its own `XxxSortBy` (e.g. `PromotionSortBy`, `OrderSortBy`) matching exactly what its
 * backend's sort-by enum actually supports, and passes it as this type parameter. A shared union
 * papers over the fact that "sort by name" means a different backend enum member per service
 * (`order-service`'s is `CUSTOMER_NAME`, not `NAME` — see CLAUDE.md) — pretending it's one shared
 * concept is exactly what caused that mismatch to go unnoticed.
 */
export interface PaginationParams<SortBy extends string = string> {
	/** Page forward from this cursor. Mutually exclusive with `before` — never send both. */
	after?: string;
	/** Page backward from this cursor. Mutually exclusive with `after` — never send both. */
	before?: string;
	sortBy?: SortBy;
	sortDir?: SortDirection;
	/** Rows per page. Omitted defaults to the backend's `PaginationConstant.DEFAULT_PAGE_SIZE` (3); the backend clamps anything over `MAX_PAGE_SIZE` (50) rather than rejecting it. */
	pageSize?: number;
}

export interface Page<T> {
	items: T[];
	nextCursor: string | null;
	prevCursor: string | null;
}
