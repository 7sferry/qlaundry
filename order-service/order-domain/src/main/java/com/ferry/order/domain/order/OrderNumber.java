package com.ferry.order.domain.order;

import com.ferry.common.CrockfordBase32;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderNumber(String value){
	private static final ZoneId BUSINESS_ZONE = ZoneOffset.UTC;
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(BUSINESS_ZONE);
	private static final SecureRandom RANDOM = new SecureRandom();

	public OrderNumber{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("Order number must not be blank");
		}
	}

	public static OrderNumber generate(Instant createdAt){
		byte[] bytes = new byte[6];
		RANDOM.nextBytes(bytes);
		long number = LocalTime.now().toNanoOfDay() / 1000000L;
		return new OrderNumber("INV-" + DATE_FORMAT.format(createdAt) + '-' +
				CrockfordBase32.encode(number, 6) +
				CrockfordBase32.encode(bytes));
	}

}
