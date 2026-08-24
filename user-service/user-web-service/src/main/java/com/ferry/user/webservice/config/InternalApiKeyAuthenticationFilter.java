package com.ferry.user.webservice.config;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class InternalApiKeyAuthenticationFilter implements Filter{

	private static final String INTERNAL_PATH_PREFIX = "/internal/";
	private static final String API_KEY_HEADER = "X-Internal-Api-Key";
	private static final String AUTHORITY_PREFIX = "SERVICE_";
	private static final int CREDENTIAL_PARTS = 3;
	// no real secret can hash to this, so an unknown client or version still runs a full-length compare
	// instead of returning early — otherwise response timing would reveal which versions exist
	private static final String ABSENT_DIGEST = "0".repeat(64);

	private final InternalKeyResolver internalKeyResolver;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		HttpServletRequest req = (HttpServletRequest) request;
		if(!req.getRequestURI().startsWith(INTERNAL_PATH_PREFIX)){
			chain.doFilter(request, response);
			return;
		}
		String presented = req.getHeader(API_KEY_HEADER);
		if(presented == null){
			chain.doFilter(request, response);
			return;
		}
		String[] parts = presented.split(":", CREDENTIAL_PARTS);
		if(parts.length != CREDENTIAL_PARTS){
			chain.doFilter(request, response);
			return;
		}
		String clientId = parts[0];
		String version = parts[1];
		String expected = internalKeyResolver.digestOf(clientId, version)
				.orElse(ABSENT_DIGEST);
		if(!matches(parts[2], expected)){
			log.warn("rejected internal call to {} presenting {}:{}", req.getRequestURI(), clientId, version);
			chain.doFilter(request, response);
			return;
		}
		log.info("internal call to {} authenticated as {} using key {}", req.getRequestURI(), clientId, version);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(clientId, null,
				List.of(new SimpleGrantedAuthority(AUTHORITY_PREFIX + clientId.toUpperCase(Locale.ROOT))));
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(request, response);
	}

	// constant-time compare so a wrong key cannot be recovered byte by byte from response timing
	private boolean matches(String secret, String expectedDigest){
		return MessageDigest.isEqual(digest(secret).getBytes(StandardCharsets.UTF_8),
				expectedDigest.getBytes(StandardCharsets.UTF_8));
	}

	// the secret itself is never stored on this side — only its digest is configured or cached, so a dump of
	// the yaml or of Redis yields nothing usable. A plain SHA-256 is enough because the secret is high-entropy
	// random, not a password
	@SneakyThrows
	private String digest(String secret){
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
				.digest(secret.getBytes(StandardCharsets.UTF_8)));
	}

}
