package com.ferry.order.core.order.process;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderProcessRequest(@NotBlank String orderId, String staffNotes) implements OrderValidation{
}
