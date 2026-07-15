package com.ferry.user.webservice.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Configuration
public class UserSecurityConfig{

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http){
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
						.requestMatchers("/auth/staff/login", "/auth/staff/refresh", "/auth/staff/registration", "/auth/tenant/registration")
						.permitAll()
						.anyRequest()
						.authenticated())
//				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
