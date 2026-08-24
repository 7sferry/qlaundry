package com.ferry.order.domain.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum SessionType{
	STAFF((short) 1),
	CUSTOMER((short) 2),
	;

	private final short value;
}
