package com.ferry.order.core.order.schedule;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.Pattern;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderScheduleRequest(@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String date) implements OrderValidation{
}
