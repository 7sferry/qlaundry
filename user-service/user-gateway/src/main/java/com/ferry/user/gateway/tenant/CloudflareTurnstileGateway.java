package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.registration.TurnstileVerificationGateway;
import com.ferry.utils.json.JsonManager;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class CloudflareTurnstileGateway implements TurnstileVerificationGateway{
	private final HttpClient httpClient;
	private final JsonManager jsonManager;
	private final String secretKey;
	private final String verifyUrl;

	public CloudflareTurnstileGateway(JsonManager jsonManager, String secretKey, String verifyUrl){
		this.httpClient = HttpClient.newHttpClient();
		this.jsonManager = jsonManager;
		this.secretKey = secretKey;
		this.verifyUrl = verifyUrl;
	}

	@Override
	public boolean verify(String token){
		if(token == null || token.isBlank()){
			return false;
		}
		try{
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(verifyUrl))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.POST(HttpRequest.BodyPublishers.ofString(buildBody(token)))
					.build();
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			TurnstileVerifyResponse parsed = jsonManager.readValue(response.body(), TurnstileVerifyResponse.class);
			return parsed != null && parsed.success();
		}catch(IOException e){
			return false;
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			return false;
		}
	}

	private String buildBody(String token){
		return "secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8)
				+ "&response=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
	}

}
