package com.ferry.user.core.staff.constant;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class LoginConstant{
	public static final String ATTEMPT_KEY = "login:attempt:";
	public static final String OTP_ATTEMPT_KEY = "login:otp-attempt:";
	public static final int CAPTCHA_AFTER_ATTEMPTS = 3;
	public static final int OTP_AFTER_ATTEMPTS = 7;
	public static final int OTP_MAX_FAILED_ATTEMPTS = 5;
	public static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(15);
}
