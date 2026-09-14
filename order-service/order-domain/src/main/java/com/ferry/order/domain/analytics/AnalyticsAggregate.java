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
public enum AnalyticsAggregate{
	ORDER((short) 1),
	LAUNDRY_SERVICE((short) 2),
	;

	private static final Map<Short,AnalyticsAggregate> AGGREGATE_MAP = Stream.of(AnalyticsAggregate.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<AnalyticsAggregate> fromValue(short value){
		return Optional.ofNullable(AGGREGATE_MAP.get(value));
	}

}
