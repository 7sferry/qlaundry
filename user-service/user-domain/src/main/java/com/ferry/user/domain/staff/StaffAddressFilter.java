package com.ferry.user.domain.staff;

import lombok.Builder;

import java.util.Collection;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record StaffAddressFilter(String staffId, Collection<String> staffIds){
}
