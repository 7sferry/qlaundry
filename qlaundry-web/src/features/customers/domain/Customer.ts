/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export type CustomerTier = 'bronze' | 'silver' | 'gold' | 'platinum';

export interface Customer {
	id: string;
	fullName: string;
	phone: string;
	email?: string;
	address: string;
	notes?: string;
	totalOrders: number;
	totalSpend: number;
	tier: CustomerTier;
	joinedAt: string;
	lastOrderAt?: string;
}

export interface CreateCustomerInput {
	fullName: string;
	phone: string;
	email?: string;
	address: string;
	notes?: string;
}

export interface UpdateCustomerInput extends Partial<CreateCustomerInput> {
	id: string;
}

export const TIER_LABELS: Record<CustomerTier, string> = {
	bronze: 'Bronze',
	silver: 'Silver',
	gold: 'Gold',
	platinum: 'Platinum',
};

export const TIER_THRESHOLDS: Record<CustomerTier, number> = {
	bronze: 0,
	silver: 500000,
	gold: 2000000,
	platinum: 5000000,
};

export function computeTier(totalSpend: number): CustomerTier {
	if (totalSpend >= TIER_THRESHOLDS.platinum) return 'platinum';
	if (totalSpend >= TIER_THRESHOLDS.gold) return 'gold';
	if (totalSpend >= TIER_THRESHOLDS.silver) return 'silver';
	return 'bronze';
}
