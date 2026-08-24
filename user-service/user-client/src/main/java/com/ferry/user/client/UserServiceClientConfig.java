package com.ferry.user.client;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record UserServiceClientConfig(String baseUrl, String apiKey, Duration timeout){
}
