package com.ferry.order.domain.order;

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
public enum ClothingType{
	SHIRT((short) 1),
	PANTS((short) 2),
	DRESS((short) 3),
	JACKET((short) 4),
	BED_LINEN((short) 5),
	TOWEL((short) 6),
	UNIFORM((short) 7),
	OTHER((short) 8),
	;

	private static final Map<Short,ClothingType> CLOTHING_TYPE_MAP = Stream.of(ClothingType.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<ClothingType> fromValue(short value){
		return Optional.ofNullable(CLOTHING_TYPE_MAP.get(value));
	}

}
