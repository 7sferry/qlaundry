package com.ferry.user.webservice.config;

import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.utils.cache.CacheHandler;
import com.ferry.user.webservice.tools.DefaultTokenProcessor;
import com.ferry.utils.token.DefaultTokenGenerator;
import com.ferry.utils.token.DefaultTokenParser;
import com.ferry.utils.token.TokenParser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Configuration
public class UserSecurityConfig{

	private static final String[] ALLOWED_MATCHERS = {
			"/auth/tenant/registration",
			"/auth/staff/login",
			"/auth/staff/logout",
			"/auth/staff/refresh",
			"/auth/staff/forgottenPassword",
			"/auth/staff/submitOtp",
			"/auth/tenant/confirmRegistration",
			"/auth/tenant/resendConfirmation",
			"/auth/staff/resetPassword"
	};

	@Bean
	UserJwtAuthenticationFilter jwtAuthenticationFilter(TokenParser tokenParser){
		return new UserJwtAuthenticationFilter(tokenParser);
	}

	@Bean
	FilterRegistrationBean<UserJwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
			UserJwtAuthenticationFilter jwtAuthenticationFilter){
		FilterRegistrationBean<UserJwtAuthenticationFilter> registration =
				new FilterRegistrationBean<>(jwtAuthenticationFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	InternalKeyResolver internalKeyResolver(InternalKeysProperties internalKeysProperties, CacheHandler cacheHandler){
		return new InternalKeyResolver(internalKeysProperties, cacheHandler);
	}

	@Bean
	InternalApiKeyAuthenticationFilter internalApiKeyAuthenticationFilter(InternalKeyResolver internalKeyResolver){
		return new InternalApiKeyAuthenticationFilter(internalKeyResolver);
	}

	@Bean
	FilterRegistrationBean<InternalApiKeyAuthenticationFilter> internalApiKeyAuthenticationFilterRegistration(
			InternalApiKeyAuthenticationFilter internalApiKeyAuthenticationFilter){
		FilterRegistrationBean<InternalApiKeyAuthenticationFilter> registration =
				new FilterRegistrationBean<>(internalApiKeyAuthenticationFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	@SneakyThrows
	TokenParser tokenParser(@Value("${app.token.public-key}") String base64PublicKey){
		PublicKey publicKey = KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey)));
		return new DefaultTokenParser(publicKey);
	}

	@SneakyThrows
	@Bean
	TokenProcessor tokenGenerator(@Value("${app.token.private-key}") String base64PrivateKey,
	                              @Value("${app.token.refresh-duration-in-seconds}") long refreshDurationInSeconds,
	                              @Value("${app.token.rotation-duration-before-expire-in-seconds}") long rotationDurationBeforeExpireInSeconds,
	                              @Value("${app.token.access-duration-in-seconds}") long accessDurationInSeconds){
		PrivateKey privateKey = KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey)));
		DefaultTokenGenerator tokenManager = new DefaultTokenGenerator(privateKey);
		return new DefaultTokenProcessor(tokenManager, refreshDurationInSeconds, accessDurationInSeconds,
				rotationDurationBeforeExpireInSeconds);
	}
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, UserJwtAuthenticationFilter jwtAuthenticationFilter,
	                                InternalApiKeyAuthenticationFilter internalApiKeyAuthenticationFilter){
		return http.csrf(AbstractHttpConfigurer::disable)
				.cors(corsConfigurer -> corsConfigurer.configurationSource(_ -> {
					CorsConfiguration config = new CorsConfiguration();
					config.setAllowedOrigins(List.of("http://localhost:8100"));
					config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE"));
					config.setAllowedHeaders(List.of("*"));
					config.setAllowCredentials(true);
					return config;
				}))
				.exceptionHandling(ex ->
						ex.authenticationEntryPoint((_, response, _) ->
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
				.sessionManagement(sessionManagementConfigurer ->
						sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(matcherRegistry -> matcherRegistry
						.requestMatchers(ALLOWED_MATCHERS)
						.permitAll()
						.anyRequest()
						.authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(internalApiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

}
