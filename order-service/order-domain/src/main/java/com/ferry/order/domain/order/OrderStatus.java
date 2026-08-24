package com.ferry.order.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum OrderStatus{
	PENDING((short) 1),
	CONFIRMED((short) 2),
	PICKED_UP((short) 3),
	IN_PROGRESS((short) 4),
	READY((short) 5),
	OUT_FOR_DELIVERY((short) 6),
	COMPLETED((short) 7),
	CANCELLED((short) 8),
	;

	private static final Map<Short,OrderStatus> ORDER_STATUS_MAP = Stream.of(OrderStatus.values())
			.collect(Collectors.collectingAndThen(Collectors.toMap(o -> o.value, o -> o),
					Collections::unmodifiableMap));

	private final short value;

	public static Optional<OrderStatus> fromValue(short value){
		return Optional.ofNullable(ORDER_STATUS_MAP.get(value));
	}

	public boolean canTransitionTo(OrderStatus next){
		return nextStatuses().contains(next);
	}

	public boolean isClosed(){
		return this == COMPLETED || this == CANCELLED;
	}

	private Set<OrderStatus> nextStatuses(){
		return switch(this){
			case PENDING -> Set.of(CONFIRMED, CANCELLED);
			case CONFIRMED -> Set.of(PICKED_UP, IN_PROGRESS, CANCELLED);
			case PICKED_UP -> Set.of(IN_PROGRESS, CANCELLED);
			case IN_PROGRESS -> Set.of(READY, CANCELLED);
			case READY -> Set.of(OUT_FOR_DELIVERY, COMPLETED, CANCELLED);
			case OUT_FOR_DELIVERY -> Set.of(COMPLETED, CANCELLED);
			case COMPLETED, CANCELLED -> Set.of();
		};
	}

}
