package com.ferry.order.domain.analytics;

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
public enum AnalyticsEventStatus{
	CREATED((short) 1),
	PUBLISHED((short) 2),
	;

	private static final Map<Short,AnalyticsEventStatus> STATUS_MAP = Stream.of(AnalyticsEventStatus.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<AnalyticsEventStatus> fromValue(short value){
		return Optional.ofNullable(STATUS_MAP.get(value));
	}

}
