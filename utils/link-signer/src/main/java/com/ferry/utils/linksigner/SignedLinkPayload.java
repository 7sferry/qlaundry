package com.ferry.utils.linksigner;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record SignedLinkPayload(Map<String, String> fields, long expiresAt){
}
