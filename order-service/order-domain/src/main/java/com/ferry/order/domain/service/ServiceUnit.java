package com.ferry.order.domain.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum ServiceUnit{
	KG((short) 1),
	ITEM((short) 2),
	LOAD((short) 3),
	SET((short) 4),
	;

	private static final Map<Short,ServiceUnit> SERVICE_UNIT_MAP = Stream.of(ServiceUnit.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<ServiceUnit> fromValue(short value){
		return Optional.ofNullable(SERVICE_UNIT_MAP.get(value));
	}

	public boolean isWeighed(){
		return this == KG;
	}

}
