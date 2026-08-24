package com.ferry.order.webservice.service.create;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceCreateWebResponse(String id, String name, String description, BigDecimal pricePerUnit,
                                              String unit, String category, int estimatedHours,
                                              double expressMultiplier, boolean popular, boolean active){
}
