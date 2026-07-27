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
	STAFF((short) 1),
	CUSTOMER((short) 2),
	;

	private static final Map<Short,SessionType> SESSION_TYPE_MAP = Stream.of(SessionType.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<SessionType> fromValue(short value){
		return Optional.ofNullable(SESSION_TYPE_MAP.get(value));
	}

}
