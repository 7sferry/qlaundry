package com.ferry.user.core.tenant.registration;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantRegistrationRequest(String fullName, String tenantName, String description, String username, String password,
                                        List<String> emails, List<String> phones, List<String> addresses,
                                        String captchaToken){
}
