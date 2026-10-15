package com.ferry.utils.httpclient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

class BufferedClientHttpResponse implements ClientHttpResponse{

	private final HttpStatusCode statusCode;
	private final String statusText;
	private final HttpHeaders headers;
	private final byte[] body;

	BufferedClientHttpResponse(HttpStatusCode statusCode, String statusText, HttpHeaders headers, byte[] body){
		this.statusCode = statusCode;
		this.statusText = statusText;
		this.headers = HttpHeaders.copyOf(headers);
		this.body = body;
	}

	@Override
	public HttpStatusCode getStatusCode(){
		return statusCode;
	}

	@Override
	public String getStatusText(){
		return statusText;
	}

	@Override
	public HttpHeaders getHeaders(){
		return headers;
	}

	@Override
	public InputStream getBody(){
		return new ByteArrayInputStream(body);
	}

	@Override
	public void close(){
	}

}
