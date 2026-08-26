package com.ferry.promotion.domain.promotion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum PromotionRejection{
	NOT_FOUND("Promotion code is not recognised"),
	INACTIVE("Promotion is no longer active"),
	NOT_STARTED("Promotion has not started yet"),
	EXPIRED("Promotion has expired"),
	EXHAUSTED("Promotion has reached its usage limit"),
	NO_DISCOUNT("Promotion does not discount this order"),
	NOT_COMBINABLE("Promotion cannot be combined with other promo codes"),
	BELOW_MIN_SUBTOTAL("Order subtotal is below the minimum required for this promotion"),
	;

	private final String message;
}
