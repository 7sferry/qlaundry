package com.ferry.promotion.domain.analytics;

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
public enum PromotionAnalyticsAggregate{
	PROMOTION((short) 1),
	;

	private static final Map<Short, PromotionAnalyticsAggregate> AGGREGATE_MAP = Map.copyOf(Stream.of(PromotionAnalyticsAggregate.values())
			.collect(Collectors.toMap(o -> o.value, o -> o)));

	private final short value;

	public static Optional<PromotionAnalyticsAggregate> fromValue(short value){
		return Optional.ofNullable(AGGREGATE_MAP.get(value));
	}

}
