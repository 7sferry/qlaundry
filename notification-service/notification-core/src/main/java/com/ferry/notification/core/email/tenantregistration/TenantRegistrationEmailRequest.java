package com.ferry.notification.core.email.tenantregistration;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantRegistrationEmailRequest(String triggerId, String recipient, String staffFullName,
                                             String staffUsername, String tenantName, String tenantDescription,
                                             Instant registeredAt){
}
