package com.ferry.notification.webservice.email.tenantregistration;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantRegistrationEmailMessage(String recipient, String staffFullName, String staffUsername,
                                             String tenantId, String tenantName, String tenantDescription,
                                             Instant registeredAt, String confirmationToken){
}
