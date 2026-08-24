package com.ferry.order.core.service.delete;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceDeleteRequest(@NotBlank String serviceId) implements OrderValidation{
}
