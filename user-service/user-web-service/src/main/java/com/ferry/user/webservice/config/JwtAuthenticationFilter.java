package com.ferry.user.webservice.config;

/************************
 * Made by [MR Ferry™]  *
 * on November 2025     *
 ************************/

import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.token.UserPrincipal;
import com.ferry.utils.token.TokenParser;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter{

	private static final String BEARER_PREFIX = "Bearer ";

	private final TokenParser tokenParser;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		HttpServletRequest req = (HttpServletRequest) request;
		String header = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith(BEARER_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		String accessToken = header.substring(BEARER_PREFIX.length());
		Map<String, Object> payload = tokenParser.parseToken(accessToken);
		if(payload == null){
			chain.doFilter(request, response);
			return;
		}
		UserPrincipal principal = new UserPrincipal(
				String.valueOf(payload.get("sub")),
				String.valueOf(payload.get("fullName")),
				String.valueOf(payload.get("tenantName")),
				String.valueOf(payload.get("tenantId")),
				SessionType.valueOf(String.valueOf(payload.get("type")))
		);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(request, response);
	}

}
