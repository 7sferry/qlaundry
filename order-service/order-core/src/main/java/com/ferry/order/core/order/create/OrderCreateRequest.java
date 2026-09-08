package com.ferry.order.core.order.create;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderCreateRequest(String customerId, @NotBlank String customerName, @NotBlank String customerPhone,
                                 String customerEmail, String customerAddress, @NotBlank String serviceId,
                                 @NotEmpty List<@Valid Item> items, @Positive int quantity, @Positive Double weightKg,
                                 @PositiveOrZero BigDecimal discount, OrderPriority priority,
                                 PaymentMethod paymentMethod, Long pickupAt, Long estimatedDeliveryAt,
                                 String notes, List<String> promoCodes,
                                 Boolean pickedUpImmediately) implements OrderValidation{

	public record Item(@NotNull ClothingType type, String label, @Positive int quantity){
	}

}
