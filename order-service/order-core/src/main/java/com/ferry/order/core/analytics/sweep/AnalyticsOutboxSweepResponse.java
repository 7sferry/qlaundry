package com.ferry.order.core.analytics.sweep;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record AnalyticsOutboxSweepResponse(int republished, int failed){

	public boolean isEmpty(){
		return republished == 0 && failed == 0;
	}

}
