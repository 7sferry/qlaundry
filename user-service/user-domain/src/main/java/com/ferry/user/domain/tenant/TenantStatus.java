package com.ferry.user.domain.tenant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

@Getter
@RequiredArgsConstructor
public enum TenantStatus{
	PENDING((short) 0),
	ACTIVE((short) 1),
	;

	private final short value;

	public static Optional<TenantStatus> findByValue(short value){
		for(TenantStatus status : TenantStatus.values()){
			if(status.value == value){
				return Optional.of(status);
			}
		}
		return Optional.empty();
	}
}
