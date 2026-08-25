package com.ferry.utils.linksigner;

import java.util.Map;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LinkSigner{
	String sign(long expiresAt, Map<String, String> fields);

	Optional<SignedLinkPayload> verify(String token);
}
