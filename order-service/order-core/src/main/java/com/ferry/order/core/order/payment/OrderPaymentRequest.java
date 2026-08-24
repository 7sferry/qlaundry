package com.ferry.order.core.order.payment;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.order.PaymentMethod;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderPaymentRequest(@NotBlank String orderId, PaymentMethod paymentMethod) implements OrderValidation{
}
