package com.ferry.user.webservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ConfigurationProperties("app.internal")
public record InternalKeysProperties(Map<String, Map<String, String>> clients){
}
