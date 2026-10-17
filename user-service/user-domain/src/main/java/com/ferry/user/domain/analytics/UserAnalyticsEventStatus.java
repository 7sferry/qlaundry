package com.ferry.user.domain.analytics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum UserAnalyticsEventStatus{
	CREATED((short) 1),
	PUBLISHED((short) 2),
	;

	private static final Map<Short, UserAnalyticsEventStatus> STATUS_MAP = Map.copyOf(Stream.of(UserAnalyticsEventStatus.values())
			.collect(Collectors.toMap(o -> o.value, Function.identity())));

	private final short value;

	public static Optional<UserAnalyticsEventStatus> fromValue(short value){
		return Optional.ofNullable(STATUS_MAP.get(value));
	}

}
