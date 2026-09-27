package com.ferry.order.domain.order;

import com.ferry.common.CrockfordBase32;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;

import java.security.SecureRandom;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderNumber(String value){
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final SecureRandom RANDOM = new SecureRandom();

	public OrderNumber{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("Order number must not be blank");
		}
	}

	public static OrderNumber generate(ZoneId zone){
		byte[] bytes = new byte[6];
		RANDOM.nextBytes(bytes);
		ZonedDateTime now = ZonedDateTime.now(zone);
		long number = now.toLocalTime().toNanoOfDay() / 1000000L;
		return new OrderNumber("INV-" + DATE_FORMAT.format(now) + '-' +
				CrockfordBase32.encode(number, 6) +
				CrockfordBase32.encode(bytes));
	}

}
