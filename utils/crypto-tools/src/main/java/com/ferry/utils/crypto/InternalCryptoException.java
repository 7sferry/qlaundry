package com.ferry.utils.crypto;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class InternalCryptoException extends RuntimeException{
	public InternalCryptoException(String message){
		super(message);
	}

	public InternalCryptoException(String message, Throwable cause){
		super(message, cause);
	}
}
