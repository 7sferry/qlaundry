package com.ferry.notification.domain;

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
 * on Juli 2026         *
 ************************/

@Getter
@RequiredArgsConstructor
public enum EmailType{
	TENANT_REGISTRATION((short) 1),
	FORGOTTEN_PASSWORD((short) 2),
	PASSWORD_CHANGED((short) 3),
	;

	private static final Map<Short,EmailType> EMAIL_TYPE_MAP = Map.copyOf(Stream.of(EmailType.values())
			.collect(Collectors.toMap(o -> o.value, Function.identity())));

	private final short value;

	public static Optional<EmailType> fromValue(short value){
		return Optional.ofNullable(EMAIL_TYPE_MAP.get(value));
	}

}
