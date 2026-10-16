package com.ferry.order.domain.machine;

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
public enum MachineType{
	WASHER((short) 1),
	DRYER((short) 2),
	IRONER((short) 3),
	;

	private static final Map<Short,MachineType> MACHINE_TYPE_MAP = Stream.of(MachineType.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<MachineType> fromValue(short value){
		return Optional.ofNullable(MACHINE_TYPE_MAP.get(value));
	}

}
