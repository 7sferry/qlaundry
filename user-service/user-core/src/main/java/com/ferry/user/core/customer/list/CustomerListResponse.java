package com.ferry.user.core.customer.list;

import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerListResponse(List<CustomerDomain> customers,
                                   Map<String, List<CustomerEmailDomain>> emailsByCustomerId,
                                   Map<String, List<CustomerPhoneDomain>> phonesByCustomerId,
                                   Map<String, List<CustomerAddressDomain>> addressesByCustomerId,
                                   String nextCursor, String prevCursor){
}
