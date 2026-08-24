package com.ferry.order.core.order.detail;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderDetailRequest(@NotBlank String orderId) implements OrderValidation{
}
