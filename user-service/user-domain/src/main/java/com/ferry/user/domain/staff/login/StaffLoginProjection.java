package com.ferry.user.domain.staff.login;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffLoginProjection(String id, String username, String password, String fullName, String tenantId, short roleId) {
}
