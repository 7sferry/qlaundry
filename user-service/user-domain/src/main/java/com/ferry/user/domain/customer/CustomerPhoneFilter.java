package com.ferry.user.domain.customer;

import lombok.Builder;

import java.util.Collection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder
public record CustomerPhoneFilter(String customerId, Collection<String> customerIds){
}
