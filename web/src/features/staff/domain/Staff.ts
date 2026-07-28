/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export type StaffRole = 'SUPER_STAFF' | 'STAFF';

export interface Staff {
	id: string;
	username: string;
	fullName: string;
	description?: string;
	emails: string[];
	phones: string[];
	addresses: string[];
	joinedAt: string;
}

export interface CreateStaffInput {
	username: string;
	password: string;
	fullName: string;
	description?: string;
	role: StaffRole;
	emails: string[];
	phones: string[];
	addresses: string[];
}

export interface UpdateStaffInput extends Partial<Omit<CreateStaffInput, 'username' | 'password'>> {
	id: string;
	currentPassword?: string;
	newPassword?: string;
}
