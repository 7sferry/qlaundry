package com.ferry.user.core.staff.constant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class TokenConstant{
	public static final String REFRESH_KEY = "session:refreshToken:";
	public static final String ACCESS_KEY = "session:accessToken:";
	public static final String ROTATED_KEY = "session:rotatedToken:";
	public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
	public static final String AUTH_PATH = "/auth";
	public static final long REFRESH_CACHE_MAX_SECONDS = 3600;
	public static final long ROTATION_GRACE_SECONDS = 60;
	public static final long ACCESS_CACHE_EARLY_EXPIRY_SECONDS = 60;
}
