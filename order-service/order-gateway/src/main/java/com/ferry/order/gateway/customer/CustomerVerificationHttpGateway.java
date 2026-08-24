package com.ferry.order.gateway.customer;

import com.ferry.order.core.order.create.CustomerVerificationGateway;
import com.ferry.order.core.order.create.CustomerVerificationHttpRequest;
import com.ferry.order.domain.common.exception.CustomerVerificationException;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.json.JsonManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
public class CustomerVerificationHttpGateway implements CustomerVerificationGateway{

	private static final String API_KEY_HEADER = "X-Internal-Api-Key";
	private static final String TRACE_ID_HEADER = "X-Trace-Id";
	private static final String VERIFICATION_PATH = "/internal/customer/verification";
	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	private final HttpClient httpClient;
	private final JsonManager jsonManager;
	private final IdGenerator idGenerator;
	private final String baseUrl;
	private final String apiKey;

	public CustomerVerificationHttpGateway(JsonManager jsonManager, IdGenerator idGenerator, String baseUrl,
	                                        String apiKey){
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(TIMEOUT)
				.build();
		this.jsonManager = jsonManager;
		this.idGenerator = idGenerator;
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
	}

	@Override
	public boolean belongsToTenant(CustomerVerificationHttpRequest request){
		String traceId = idGenerator.generateId();
		try{
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + VERIFICATION_PATH
							+ "?customerId=" + encode(request.customerId())
							+ "&tenantId=" + encode(request.tenantId())))
					.header(API_KEY_HEADER, apiKey)
					.header(TRACE_ID_HEADER, traceId)
					.timeout(TIMEOUT)
					.GET()
					.build();
			log.debug("verifying customer {} for tenant {} [trace={}]", request.customerId(), request.tenantId(),
					traceId);
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() != 200){
				throw new CustomerVerificationException("Customer verification responded with status "
						+ response.statusCode() + " [trace=" + traceId + ']');
			}
			CustomerVerificationHttpResponse parsed = jsonManager.readValue(response.body(),
					CustomerVerificationHttpResponse.class);
			return parsed != null && parsed.valid();
		}catch(IOException e){
			throw new CustomerVerificationException("Customer verification is unreachable [trace=" + traceId + ']');
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new CustomerVerificationException("Customer verification was interrupted [trace=" + traceId + ']');
		}
	}

	private String encode(String value){
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

}
