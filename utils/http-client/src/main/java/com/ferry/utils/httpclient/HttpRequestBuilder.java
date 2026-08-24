package com.ferry.utils.httpclient;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface HttpRequestBuilder{

	static HttpRequestBuilder get(String uri){
		return new DefaultHttpRequestBuilder("GET", uri);
	}

	static HttpRequestBuilder post(String uri){
		return new DefaultHttpRequestBuilder("POST", uri);
	}

	static HttpRequestBuilder put(String uri){
		return new DefaultHttpRequestBuilder("PUT", uri);
	}

	static HttpRequestBuilder delete(String uri){
		return new DefaultHttpRequestBuilder("DELETE", uri);
	}

	HttpRequestBuilder header(String name, String value);

	HttpRequestBuilder requestParam(Record params);

	HttpRequestBuilder requestBody(Record body);

	HttpRequestBuilder timeout(Duration timeout);

	HttpRequestExecutor build();

}
