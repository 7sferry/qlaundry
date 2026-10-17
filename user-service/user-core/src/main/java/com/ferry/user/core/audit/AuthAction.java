package com.ferry.user.core.audit;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public enum AuthAction{
	TENANT_REGISTRATION,
	TENANT_CONFIRMATION,
	TENANT_RESEND_CONFIRMATION,
	STAFF_REGISTRATION,
	STAFF_LOGIN,
	STAFF_REFRESH,
	STAFF_LOGOUT,
	STAFF_FORGOTTEN_PASSWORD,
	STAFF_SUBMIT_OTP,
	STAFF_RESET_PASSWORD,
	STAFF_CHANGE_PASSWORD
}
