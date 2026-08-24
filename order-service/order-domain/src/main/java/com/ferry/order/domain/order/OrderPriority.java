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
public enum OrderPriority{
	NORMAL((short) 1),
	EXPRESS((short) 2),
	;

	private static final Map<Short,OrderPriority> ORDER_PRIORITY_MAP = Stream.of(OrderPriority.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<OrderPriority> fromValue(short value){
		return Optional.ofNullable(ORDER_PRIORITY_MAP.get(value));
	}

}
