package com.ferry.order.core.order.saga;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderPromotionSagaSweepResponse(
	int committed,
	int released,
	int failed){

	public boolean isEmpty(){
		return committed == 0 && released == 0 && failed == 0;
	}

}
