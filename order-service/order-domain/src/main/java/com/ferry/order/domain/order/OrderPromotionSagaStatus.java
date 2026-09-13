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
 * on September 2026    *
 ************************/

@Getter
@RequiredArgsConstructor
public enum OrderPromotionSagaStatus{
	PENDING((short) 1),
	COMMITTED((short) 2),
	RELEASED((short) 3),
	;

	private static final Map<Short,OrderPromotionSagaStatus> SAGA_STATUS_MAP = Stream.of(OrderPromotionSagaStatus.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<OrderPromotionSagaStatus> fromValue(short value){
		return Optional.ofNullable(SAGA_STATUS_MAP.get(value));
	}

}
