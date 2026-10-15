package com.ferry.utils.httpclient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

import java.net.URI;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

record RedirectedHttpRequest(
	HttpMethod method,
	URI uri,
	HttpHeaders headers,
	Map<String, Object> attributes) implements HttpRequest{

	@Override
	public HttpMethod getMethod(){
		return method;
	}

	@Override
	public URI getURI(){
		return uri;
	}

	@Override
	public HttpHeaders getHeaders(){
		return headers;
	}

	@Override
	public Map<String, Object> getAttributes(){
		return attributes;
	}

}
