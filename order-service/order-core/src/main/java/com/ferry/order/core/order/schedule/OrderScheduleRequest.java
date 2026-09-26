package com.ferry.order.core.order.schedule;

import com.ferry.order.core.tools.OrderValidation;

import java.time.LocalDate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderScheduleRequest(LocalDate date, String zone) implements OrderValidation{
}
