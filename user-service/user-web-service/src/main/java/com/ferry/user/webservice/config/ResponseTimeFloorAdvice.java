package com.ferry.user.webservice.config;

import com.ferry.user.webservice.tools.RequestResponseTimeFloor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestControllerAdvice
public class ResponseTimeFloorAdvice implements ResponseBodyAdvice<Object>{

	@Override
	public boolean supports(@NonNull MethodParameter returnType,
	                        @NonNull Class<? extends HttpMessageConverter<?>> converterType){
		return true;
	}

	@Override
	public @Nullable Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
	                                        @NonNull MediaType selectedContentType,
	                                        @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
	                                        @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response){
		if(request instanceof ServletServerHttpRequest servletRequest
				&& servletRequest.getServletRequest().getAttribute(RequestResponseTimeFloor.DEADLINE_ATTRIBUTE) instanceof Long deadline){
			RequestResponseTimeFloor.awaitUntil(deadline);
		}
		return body;
	}
}
