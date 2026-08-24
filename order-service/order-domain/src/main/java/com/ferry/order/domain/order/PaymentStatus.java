package com.ferry.order.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum PaymentStatus{
	UNPAID((short) 1),
	PAID((short) 2),
	;

	private static final Map<Short,PaymentStatus> PAYMENT_STATUS_MAP = Stream.of(PaymentStatus.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<PaymentStatus> fromValue(short value){
		return Optional.ofNullable(PAYMENT_STATUS_MAP.get(value));
	}

}
