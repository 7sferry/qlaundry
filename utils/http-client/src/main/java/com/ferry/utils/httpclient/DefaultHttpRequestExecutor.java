package com.ferry.utils.httpclient;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
class DefaultHttpRequestExecutor implements HttpRequestExecutor{

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	private final HttpRequest request;
	private final String traceId;

	DefaultHttpRequestExecutor(HttpRequest request, String traceId){
		this.request = request;
		this.traceId = traceId;
	}

	@Override
	public String traceId(){
		return traceId;
	}

	@Override
	public <T> T send(Class<T> responseType){
		try{
			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() < 200 || response.statusCode() >= 300){
				throw new HttpClientException("Request to " + request.uri() + " responded with status "
						+ response.statusCode() + " [trace=" + traceId + ']' + response.body(), response.statusCode(), traceId);
			}
			return read(response.body(), responseType);
		}catch(IOException e){
			log.warn("request to {} is unreachable [trace={}]", request.uri(), traceId);
			throw new HttpClientException("Request to " + request.uri() + " is unreachable [trace=" + traceId + ']',
					-1, traceId, e);
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new HttpClientException("Request to " + request.uri() + " was interrupted [trace=" + traceId + ']',
					-1, traceId, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T read(String body, Class<T> responseType){
		if(responseType == String.class){
			return (T) body;
		}
		if(responseType == Void.class){
			return null;
		}
		return DefaultHttpRequestBuilder.OBJECT_MAPPER.readValue(body, responseType);
	}

}
