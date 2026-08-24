package com.ferry.utils.httpclient;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class HttpClientException extends RuntimeException{

	// -1 when the failure happened below the HTTP layer (unreachable, interrupted) rather than in the response
	private final int statusCode;
	private final String traceId;

	public HttpClientException(String message, int statusCode, String traceId){
		super(message);
		this.statusCode = statusCode;
		this.traceId = traceId;
	}

	public HttpClientException(String message, int statusCode, String traceId, Throwable cause){
		super(message, cause);
		this.statusCode = statusCode;
		this.traceId = traceId;
	}

	public int statusCode(){
		return statusCode;
	}

	public String traceId(){
		return traceId;
	}

}
