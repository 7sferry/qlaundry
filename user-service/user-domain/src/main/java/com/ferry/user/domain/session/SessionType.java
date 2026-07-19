package com.ferry.user.domain.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@RequiredArgsConstructor
public enum SessionType{
	STAFF(1),
	CUSTOMER(2),
	;

	private static final Map<Integer,SessionType> SESSION_TYPE_MAP = Stream.of(SessionType.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final int value;

	public static Optional<SessionType> fromValue(int value){
		return Optional.ofNullable(SESSION_TYPE_MAP.get(value));
	}

}
