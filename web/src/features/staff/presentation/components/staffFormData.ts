/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRole} from '../../domain/Staff';

export interface StaffFormData {
	username: string;
	password: string;
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
	fullName: '',
	description: '',
	role: 'STAFF',
	email: '',
	phone: '',
	address: '',
};
