package com.ferry.promotion.domain.promotion;

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
public enum PromotionType{
	CUMULATIVE_PERCENTAGE((short) 1),
	FIXED_AMOUNT((short) 2),
	NON_CUMULATIVE_PERCENTAGE((short) 3),
	;

	private static final Map<Short,PromotionType> PROMOTION_TYPE_MAP = Stream.of(PromotionType.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static PromotionType fromValue(short value){
		PromotionType type = PROMOTION_TYPE_MAP.get(value);
		if(type == null){
			throw new IllegalArgumentException("No such promotion type: " + value);
		}
		return type;
	}

	public boolean isPercentageBased(){
		return this == CUMULATIVE_PERCENTAGE || this == NON_CUMULATIVE_PERCENTAGE;
	}

}
