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
public enum ServiceCategory{
	WASH((short) 1),
	DRY_CLEAN((short) 2),
	IRON((short) 3),
	SPECIALTY((short) 4),
	;

	private static final Map<Short,ServiceCategory> SERVICE_CATEGORY_MAP = Stream.of(ServiceCategory.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<ServiceCategory> fromValue(short value){
		return Optional.ofNullable(SERVICE_CATEGORY_MAP.get(value));
	}

}
