package com.ferry.user.webservice.config;

import com.ferry.user.core.tools.InternalKeyConstant;
import com.ferry.utils.cache.CacheHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class InternalKeyResolver{
	private final InternalKeysProperties internalKeysProperties;
	private final CacheHandler cacheHandler;

	Optional<String> digestOf(String clientId, String version){
		try{
			return cacheHandler.get(InternalKeyConstant.KEY_PREFIX + clientId, version)
					.map(digest -> digest.trim().toLowerCase());
		}catch(RuntimeException e){
			// break-glass: only an unreachable cache falls back to the configured keys — a cache that answers
			// "no such version" is authoritative, so a revoked key stays revoked
			log.warn("internal key cache is unreachable, falling back to the configured keys", e);
			return configured(clientId, version);
		}
	}

	private Optional<String> configured(String clientId, String version){
		Map<String, Map<String, String>> clients = internalKeysProperties.clients();
		if(clients == null){
			return Optional.empty();
		}
		Map<String, String> versions = clients.get(clientId);
		if(versions == null){
			return Optional.empty();
		}
		return Optional.ofNullable(versions.get(version))
				.map(digest -> digest.trim().toLowerCase());
	}

}
