package com.ferry.order.core.order.process;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderProcessRequest(
	@NotBlank String orderId,
	String staffNotes,
	Set<String> machineIds,
	Set<String> staffIds) implements OrderValidation{
}
