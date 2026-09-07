package com.ferry.promotion.webservice.config;

import com.ferry.utils.cache.CacheHandler;
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
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Configuration
public class PromotionSecurityConfig{

	@Bean
	PromotionJwtAuthenticationFilter jwtAuthenticationFilter(TokenParser tokenParser){
		return new PromotionJwtAuthenticationFilter(tokenParser);
	}

	@Bean
	FilterRegistrationBean<PromotionJwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
			PromotionJwtAuthenticationFilter jwtAuthenticationFilter){
		FilterRegistrationBean<PromotionJwtAuthenticationFilter> registration =
				new FilterRegistrationBean<>(jwtAuthenticationFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	PromotionInternalKeyResolver internalKeyResolver(PromotionInternalKeysProperties internalKeysProperties,
	                                                 CacheHandler cacheHandler){
		return new PromotionInternalKeyResolver(internalKeysProperties, cacheHandler);
	}

	@Bean
	PromotionInternalApiKeyAuthenticationFilter internalApiKeyAuthenticationFilter(
			PromotionInternalKeyResolver internalKeyResolver){
		return new PromotionInternalApiKeyAuthenticationFilter(internalKeyResolver);
	}

	@Bean
	FilterRegistrationBean<PromotionInternalApiKeyAuthenticationFilter> internalApiKeyAuthenticationFilterRegistration(
			PromotionInternalApiKeyAuthenticationFilter internalApiKeyAuthenticationFilter){
		FilterRegistrationBean<PromotionInternalApiKeyAuthenticationFilter> registration =
				new FilterRegistrationBean<>(internalApiKeyAuthenticationFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	@SneakyThrows
	TokenParser tokenParser(@Value("${app.token.public-key}") String base64PublicKey){
		PublicKey publicKey = KeyFactory.getInstance("Ed25519")
				.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey)));
		return new DefaultTokenParser(publicKey);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, PromotionJwtAuthenticationFilter jwtAuthenticationFilter,
	                                PromotionInternalApiKeyAuthenticationFilter internalApiKeyAuthenticationFilter){
		return http.csrf(AbstractHttpConfigurer::disable)
				.cors(corsConfigurer -> corsConfigurer.configurationSource(_ -> {
					CorsConfiguration config = new CorsConfiguration();
					config.setAllowedOrigins(List.of("http://localhost:8100","https://localhost:8100"));
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
						.requestMatchers("/public/**")
						.permitAll()
						.requestMatchers("/internal/promotion/**")
						.hasAuthority("SERVICE_ORDER")
						.anyRequest()
						.authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(internalApiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

}
