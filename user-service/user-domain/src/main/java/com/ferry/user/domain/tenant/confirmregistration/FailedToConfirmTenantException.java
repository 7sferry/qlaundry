package com.ferry.user.domain.tenant.confirmregistration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

public class FailedToConfirmTenantException extends RuntimeException{
	public FailedToConfirmTenantException(String message){
		super(message);
	}

	public FailedToConfirmTenantException(Throwable cause){
		super(cause);
	}
}
