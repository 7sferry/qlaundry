package com.ferry.analytics.core.constant;

import java.math.RoundingMode;
import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public class AnalyticsConstant{
	public static final int PERCENTAGE_SCALE = 2;
	public static final RoundingMode PERCENTAGE_ROUNDING = RoundingMode.HALF_EVEN;
	public static final int MONEY_SCALE = 2;
	public static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_EVEN;
	public static final int TOP_CUSTOMER_LIMIT = 5;
	public static final int CUSTOMER_TOTALS_MAX_IDS = 100;
	public static final Duration EXPIRING_SOON_WINDOW = Duration.ofDays(7);
}
