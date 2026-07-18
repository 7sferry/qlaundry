package com.ferry.user.core.staff.constant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class TokenConstant{
	public static final int REFRESH_TOKEN_EXPIRATION_IN_SECONDS = 10 * 60;
	public static final int ROTATION_TOKEN_BEFORE_EXPIRE_IN_SECONDS = 3 * 60;
	public static final int ACCESS_TOKEN_EXPIRATION_IN_SECONDS = 2 * 60;
	public static final String REFRESH_KEY = "session:refreshToken";
	public static final String ACCESS_KEY = "session:accessToken:";
}
