package com.ferry.user.core.staff.update;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffUpdateRequest(String fullName, String description, String currentPassword, String newPassword,
                                 List<String> emails, List<String> phones, List<String> addresses){
}
