package com.ferry.notification.core.email.forgottenpassword;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record ForgottenPasswordEmailRequest(String triggerId, String recipient, String username, String otp){
}
