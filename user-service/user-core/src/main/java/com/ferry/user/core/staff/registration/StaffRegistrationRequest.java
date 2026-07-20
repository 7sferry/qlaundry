package com.ferry.user.core.staff.registration;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffRegistrationRequest(String username, String password, String fullName, String description,
                                       String tenantId, List<String> emails, List<String> phones,
                                       List<String> addresses){
}
