package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerDetailResponse(CustomerDomain customer, List<CustomerEmailDomain> emails,
                                     List<CustomerPhoneDomain> phones, List<CustomerAddressDomain> addresses){
}
