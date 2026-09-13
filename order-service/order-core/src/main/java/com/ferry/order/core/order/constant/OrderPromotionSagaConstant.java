package com.ferry.order.core.order.constant;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public class OrderPromotionSagaConstant{
	public static final int GRACE_PERIOD_IN_MINUTES = 90;
	public static final Duration GRACE_PERIOD = Duration.ofMinutes(GRACE_PERIOD_IN_MINUTES);
	public static final int SWEEP_BATCH_SIZE = 100;
	public static final String SWEEPER_ACTOR = "system:order-saga-sweeper";
}
