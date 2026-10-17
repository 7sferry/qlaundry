/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export interface StaffOption {
	id: string;
	username: string;
	fullName: string;
}

export interface OrderStaff {
	staffId: string;
	name: string;
}

export const UNKNOWN_STAFF_NAME = 'Former staff';
