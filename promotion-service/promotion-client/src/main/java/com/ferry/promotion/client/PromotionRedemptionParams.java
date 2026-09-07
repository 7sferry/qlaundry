package com.ferry.promotion.client;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionParams(String tenantId, Collection<String> codes, BigDecimal subtotal, String referenceId,
                                        String customerId, String redeemedBy){
}
