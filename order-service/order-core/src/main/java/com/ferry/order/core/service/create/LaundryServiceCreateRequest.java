package com.ferry.order.core.service.create;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceCreateRequest(@NotBlank String name, String description,
                                          @NotNull @Positive BigDecimal pricePerUnit, @NotNull ServiceUnit unit,
                                          @NotNull ServiceCategory category, @Positive int estimatedHours,
                                          Double expressMultiplier, boolean popular) implements OrderValidation{
}
