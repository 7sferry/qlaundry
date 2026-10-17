package com.ferry.user.core.staff.names;

import com.ferry.user.core.staff.constant.StaffConstant;
import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record StaffNamesRequest(
	@Size(max = StaffConstant.NAMES_MAX_IDS) Set<String> staffIds) implements UserValidation{

	public Set<String> sanitizedStaffIds(){
		if(staffIds == null){
			return Set.of();
		}
		return staffIds.stream()
				.filter(id -> id != null && !id.isBlank())
				.map(String::trim)
				.collect(Collectors.toUnmodifiableSet());
	}

}
