package com.ferry.user.domain.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@RequiredArgsConstructor
public enum SessionType{
	STAFF(1),
	CUSTOMER(2),
	;

	private final int value;

}
