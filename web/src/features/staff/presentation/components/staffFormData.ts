/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRole} from '../../domain/Staff';

export interface StaffFormData {
	username: string;
	password: string;
	currentPassword: string;
	newPassword: string;
	fullName: string;
	description: string;
	role: StaffRole;
	email: string;
	phone: string;
	address: string;
}

export const emptyStaffForm: StaffFormData = {
	username: '',
	password: '',
	currentPassword: '',
	newPassword: '',
	fullName: '',
	description: '',
	role: 'STAFF',
	email: '',
	phone: '',
	address: '',
};
