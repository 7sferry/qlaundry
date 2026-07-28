/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export interface StaffFormData {
	username: string;
	password: string;
	fullName: string;
	description: string;
	email: string;
	phone: string;
	address: string;
}

export const emptyStaffForm: StaffFormData = {
	username: '',
	password: '',
	fullName: '',
	description: '',
	email: '',
	phone: '',
	address: '',
};
