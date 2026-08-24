package com.ferry.order.webservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ConfigurationProperties("app.crypto")
public record CryptoKeysProperties(String activeKeyId, String blindIndexKey, boolean allowPlaintextRead,
                                   Map<String, String> keys){
}
