package com.ferry.order.domain.order;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderNumberDomain(String value){
	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Jakarta");
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(BUSINESS_ZONE);
	private static final String SUFFIX_ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
	private static final int SUFFIX_LENGTH = 6;
	private static final SecureRandom RANDOM = new SecureRandom();

	public OrderNumberDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Order number must not be blank");
		}
	}

	public static OrderNumberDomain generate(Instant createdAt){
		StringBuilder suffix = new StringBuilder(SUFFIX_LENGTH);
		for(int i = 0; i < SUFFIX_LENGTH; i++){
			suffix.append(SUFFIX_ALPHABET.charAt(RANDOM.nextInt(SUFFIX_ALPHABET.length())));
		}
		return new OrderNumberDomain("INV-" + DATE_FORMAT.format(createdAt) + '-' + suffix);
	}

}
