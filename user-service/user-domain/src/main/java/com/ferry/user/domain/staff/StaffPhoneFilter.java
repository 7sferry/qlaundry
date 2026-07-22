package com.ferry.user.domain.staff;

import lombok.Builder;

import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record StaffPhoneFilter(String staffId, Collection<String> staffIds){
}
