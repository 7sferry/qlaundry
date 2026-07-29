package com.ferry.user.domain.notification;

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
public enum EmailTriggerStatus{
	CREATED((short) 1),
	PUBLISHED((short) 2),
	;

	private static final Map<Short,EmailTriggerStatus> EMAIL_TRIGGER_STATUS_MAP = Stream.of(EmailTriggerStatus.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<EmailTriggerStatus> fromValue(short value){
		return Optional.ofNullable(EMAIL_TRIGGER_STATUS_MAP.get(value));
	}

}
