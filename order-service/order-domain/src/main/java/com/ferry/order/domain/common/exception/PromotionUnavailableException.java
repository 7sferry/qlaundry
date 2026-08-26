package com.ferry.order.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class PromotionUnavailableException extends RuntimeException{
	public PromotionUnavailableException(String message, Throwable cause){
		super(message, cause);
	}
}
