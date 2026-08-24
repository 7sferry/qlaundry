package com.ferry.order.gateway.customer;

import com.ferry.order.core.order.create.CustomerVerificationGateway;
import com.ferry.order.core.order.create.CustomerVerificationHttpRequest;
import com.ferry.order.domain.common.exception.CustomerVerificationException;
import com.ferry.utils.json.JsonManager;

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

public class CustomerVerificationHttpGateway implements CustomerVerificationGateway{

	private static final String API_KEY_HEADER = "X-Internal-Api-Key";
	private static final String VERIFICATION_PATH = "/internal/customer/verification";
	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	private final HttpClient httpClient;
	private final JsonManager jsonManager;
	private final String baseUrl;
	private final String apiKey;

	public CustomerVerificationHttpGateway(JsonManager jsonManager, String baseUrl, String apiKey){
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(TIMEOUT)
				.build();
		this.jsonManager = jsonManager;
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
	}

	@Override
	public boolean belongsToTenant(CustomerVerificationHttpRequest request){
		try{
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + VERIFICATION_PATH
							+ "?customerId=" + encode(request.customerId())
							+ "&tenantId=" + encode(request.tenantId())))
					.header(API_KEY_HEADER, apiKey)
					.timeout(TIMEOUT)
					.GET()
					.build();
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() != 200){
				throw new CustomerVerificationException("Customer verification responded with status "
						+ response.statusCode());
			}
			CustomerVerificationHttpResponse parsed = jsonManager.readValue(response.body(),
					CustomerVerificationHttpResponse.class);
			return parsed != null && parsed.valid();
		}catch(IOException e){
			throw new CustomerVerificationException("Customer verification is unreachable");
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new CustomerVerificationException("Customer verification was interrupted");
		}
	}

	private String encode(String value){
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

}
