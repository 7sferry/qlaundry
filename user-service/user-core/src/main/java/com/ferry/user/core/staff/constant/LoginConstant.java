package com.ferry.user.core.staff.constant;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class LoginConstant{
	public static final String ATTEMPT_KEY = "login:attempt:";
	public static final String LOCK_KEY = "login:lock:";
	public static final int MAX_FAILED_ATTEMPTS = 3;
	public static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(5);
	public static final Duration LOCK_DURATION = Duration.ofMinutes(10);
}
