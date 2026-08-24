package com.ferry.order.domain.staff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@RequiredArgsConstructor
public enum StaffRole{
	SUPER_STAFF((short) 1),
	STAFF((short) 2),
	;

	private final short value;

	public static Optional<StaffRole> findByValue(short value){
		for(StaffRole staffRole : StaffRole.values()){
			if(staffRole.value == value){
				return Optional.of(staffRole);
			}
		}
		return Optional.empty();
	}
}
