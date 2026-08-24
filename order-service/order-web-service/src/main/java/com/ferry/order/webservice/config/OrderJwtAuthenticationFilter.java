package com.ferry.order.webservice.config;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import com.ferry.order.core.tools.EnumParser;
import com.ferry.order.domain.session.SessionType;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.token.TokenParser;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderJwtAuthenticationFilter implements Filter{

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
		if(payload.isEmpty()){
			chain.doFilter(request, response);
			return;
		}
		Optional<SessionType> sessionType = EnumParser.parse(SessionType.class,
				String.valueOf(payload.get("type")));
		Optional<StaffRole> role = EnumParser.parse(StaffRole.class, String.valueOf(payload.get("role")));
		if(sessionType.isEmpty() || role.isEmpty()){
			chain.doFilter(request, response);
			return;
		}
		OrderAuthPrincipal principal = new OrderAuthPrincipal(
				String.valueOf(payload.get("userId")),
				String.valueOf(payload.get("sub")),
				String.valueOf(payload.get("fullName")),
				String.valueOf(payload.get("tenantName")),
				String.valueOf(payload.get("tenantId")),
				sessionType.get(),
				role.get()
		);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(request, response);
	}

}
