package com.ferry.order.domain.order;

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
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum PaymentMethod{
	CASH((short) 1),
	;

	private static final Map<Short,PaymentMethod> PAYMENT_METHOD_MAP = Map.copyOf(Stream.of(PaymentMethod.values())
			.collect(Collectors.toMap(o -> o.value, Function.identity())));

	private final short value;

	public static Optional<PaymentMethod> fromValue(short value){
		return Optional.ofNullable(PAYMENT_METHOD_MAP.get(value));
	}

	public static Optional<PaymentMethod> fromName(String name){
		return Stream.of(PaymentMethod.values())
				.filter(paymentMethod -> paymentMethod.name().equalsIgnoreCase(name))
				.findFirst();
	}

}
