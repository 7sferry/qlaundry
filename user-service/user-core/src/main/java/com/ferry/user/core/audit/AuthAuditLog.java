package com.ferry.user.core.audit;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface AuthAuditLog{
	void record(AuthAction action, AuthResult result, String subject);
}
