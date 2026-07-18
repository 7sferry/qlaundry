package com.ferry.utils.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TokenParser{
	Map<String, Object> parseToken(String token);
}
