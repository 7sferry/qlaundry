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
public enum EmailTriggerType{
	TENANT_REGISTRATION((short) 1),
	FORGOTTEN_PASSWORD((short) 2),
	;

	private static final Map<Short,EmailTriggerType> EMAIL_TRIGGER_TYPE_MAP = Stream.of(EmailTriggerType.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<EmailTriggerType> fromValue(short value){
		return Optional.ofNullable(EMAIL_TRIGGER_TYPE_MAP.get(value));
	}

}
