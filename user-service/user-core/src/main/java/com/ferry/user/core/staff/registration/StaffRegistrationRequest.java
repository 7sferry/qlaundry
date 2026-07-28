package com.ferry.user.core.staff.registration;

import com.ferry.user.domain.staff.StaffRole;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffRegistrationRequest(String username, String password, String fullName, String description,
                                       StaffRole role, List<String> emails, List<String> phones,
                                       List<String> addresses){
}
