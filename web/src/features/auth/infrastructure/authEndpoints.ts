/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

export const AUTH_ENDPOINTS = {
	login: '/auth/staff/login',
	logout: '/auth/staff/logout',
	forgottenPassword: '/auth/staff/forgottenPassword',
	submitOtp: '/auth/staff/submitOtp',
	resetPassword: '/auth/staff/resetPassword',
	tenantRegistration: '/auth/tenant/registration',
	confirmTenantRegistration: '/auth/tenant/confirmRegistration',
	resendTenantConfirmation: '/auth/tenant/resendConfirmation',
	staffDetail: '/staff/detail',
} as const;
