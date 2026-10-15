package com.ferry.utils.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Slf4j
class InternalServiceClientInterceptor implements ClientHttpRequestInterceptor{

	private static final String TRACE_ID_HEADER = "X-Trace-Id";
	private static final String TRACE_ID_MDC_KEY = "traceId";
	private static final int MAX_REDIRECTS = 5;
	private static final Set<Integer> FOLLOWED_STATUSES = Set.of(301, 302, 303, 307, 308);
	private static final List<String> CREDENTIAL_HEADERS = List.of("X-Internal-Api-Key", HttpHeaders.AUTHORIZATION,
			HttpHeaders.PROXY_AUTHORIZATION, HttpHeaders.COOKIE);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution){
		String traceId = getTraceId(request);
		log.debug("sending {} {} [trace={}]", request.getMethod(), request.getURI(), traceId);
		HttpRequest currentRequest = request;
		byte[] currentBody = body;
		int redirects = 0;
		while(true){
			try(ClientHttpResponse response = execute(request, execution, currentRequest, currentBody, traceId)){
				HttpStatusCode status = response.getStatusCode();
				if(status.is2xxSuccessful()){
					if(redirects > 0){
						log.debug("received {} from {} after {} redirect(s) [trace={}]", status.value(),
								currentRequest.getURI(), redirects, traceId);
					}
					return new BufferedClientHttpResponse(status, response.getStatusText(), response.getHeaders(),
							response.getBody().readAllBytes());
				}
				if(!status.is3xxRedirection()){
					throw statusFailure(request, response, status, "", traceId);
				}
				URI target = redirectTarget(currentRequest, response);
				String refusal = refusalOf(status, currentRequest.getURI(), target, redirects);
				if(refusal != null){
					String redirect = " (redirect" + (target == null ? "" : " to " + target) + " not followed: " + refusal + ')';
					throw statusFailure(request, response, status, redirect, traceId);
				}
				boolean switchToGet = status.value() == 303 && currentRequest.getMethod() != HttpMethod.GET
						&& currentRequest.getMethod() != HttpMethod.HEAD;
				currentRequest = nextHop(currentRequest, target, status, switchToGet, traceId);
				currentBody = switchToGet ? new byte[0] : currentBody;
			}catch(IOException e){
				throw transportFailure(request, traceId, e);
			}
			redirects++;
		}
	}

	private static String getTraceId(HttpRequest request){
		String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
		if(traceId != null){
			return traceId;
		}
		String currentTraceId = MDC.get(TRACE_ID_MDC_KEY);
		String forwardedTraceId = currentTraceId != null ? currentTraceId : UUID.randomUUID().toString();
		request.getHeaders().set(TRACE_ID_HEADER, forwardedTraceId);
		return forwardedTraceId;
	}

	private @NonNull ClientHttpResponse execute(HttpRequest request, ClientHttpRequestExecution execution, HttpRequest currentRequest, byte[] currentBody, String traceId){
		try{
			return execution.execute(currentRequest, currentBody);
		}catch(IOException e){
			throw transportFailure(request, traceId, e);
		}
	}

	private URI redirectTarget(HttpRequest current, ClientHttpResponse response){
		String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
		if(location == null){
			return null;
		}
		try{
			return current.getURI().resolve(location);
		}catch(IllegalArgumentException e){
			return null;
		}
	}

	private String refusalOf(HttpStatusCode status, URI from, URI target, int redirects){
		if(!FOLLOWED_STATUSES.contains(status.value())){
			return "status is not a followable redirect";
		}
		if(target == null){
			return "no usable Location header";
		}
		if(!"http".equalsIgnoreCase(target.getScheme()) && !"https".equalsIgnoreCase(target.getScheme())){
			return "unsupported scheme";
		}
		if("https".equalsIgnoreCase(from.getScheme()) && "http".equalsIgnoreCase(target.getScheme())){
			return "https to http downgrade";
		}
		if(redirects >= MAX_REDIRECTS){
			return "more than " + MAX_REDIRECTS + " redirects";
		}
		return null;
	}

	private HttpRequest nextHop(HttpRequest current, URI target, HttpStatusCode status, boolean switchToGet,
	                            String traceId){
		HttpHeaders headers = HttpHeaders.copyOf(current.getHeaders());
		if(sameOrigin(current.getURI(), target)){
			log.debug("following {} redirect to {} [trace={}]", status.value(), target, traceId);
		}else{
			CREDENTIAL_HEADERS.forEach(headers::remove);
			log.warn("redirect from {} to {} leaves the origin, credentials dropped [trace={}]", current.getURI(), target,
					traceId);
		}
		if(switchToGet){
			headers.remove(HttpHeaders.CONTENT_TYPE);
		}
		HttpMethod method = switchToGet ? HttpMethod.GET : current.getMethod();
		return new RedirectedHttpRequest(method, target, headers, current.getAttributes());
	}

	private boolean sameOrigin(URI from, URI to){
		return from.getScheme().equalsIgnoreCase(to.getScheme())
				&& from.getHost() != null && from.getHost().equalsIgnoreCase(to.getHost())
				&& effectivePort(from) == effectivePort(to);
	}

	private int effectivePort(URI uri){
		if(uri.getPort() != -1){
			return uri.getPort();
		}
		return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
	}

	private HttpClientException statusFailure(HttpRequest request, ClientHttpResponse response, HttpStatusCode status,
	                                          String detail, String traceId){
		return new HttpClientException("Request to " + request.getURI() + " responded with status " + status.value()
				+ detail + " [trace=" + traceId + ']' + bodyOf(response), status.value(), traceId);
	}

	private String bodyOf(ClientHttpResponse response){
		try{
			return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
		}catch(IOException e){
			return "";
		}
	}

	private HttpClientException transportFailure(HttpRequest request, String traceId, IOException e){
		if(e.getCause() instanceof InterruptedException){
			return new HttpClientException("Request to " + request.getURI() + " was interrupted [trace=" + traceId + ']',
					-1, traceId, e);
		}
		log.warn("request to {} is unreachable [trace={}]", request.getURI(), traceId);
		return new HttpClientException("Request to " + request.getURI() + " is unreachable [trace=" + traceId + ']',
				-1, traceId, e);
	}

}
