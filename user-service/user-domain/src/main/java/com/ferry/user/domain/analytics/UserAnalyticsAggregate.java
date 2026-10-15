package com.ferry.user.domain.analytics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum UserAnalyticsAggregate{
	STAFF((short) 1),
	CUSTOMER((short) 2),
	;

	private static final Map<Short, UserAnalyticsAggregate> AGGREGATE_MAP = Map.copyOf(Stream.of(UserAnalyticsAggregate.values())
			.collect(Collectors.toMap(o -> o.value, o -> o)));

	private final short value;

	public static Optional<UserAnalyticsAggregate> fromValue(short value){
		return Optional.ofNullable(AGGREGATE_MAP.get(value));
	}

}
