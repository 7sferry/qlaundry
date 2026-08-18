package com.ferry.utils.crypto;

import java.nio.charset.StandardCharsets;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CryptoAad(String table, String column, String owner){
	public CryptoAad{
		if(table == null || table.isBlank() || column == null || column.isBlank()
				|| owner == null || owner.isBlank()){
			throw new IllegalArgumentException("AAD table, column, and owner must not be blank");
		}
	}

	public byte[] bytes(){
		return (table + ':' + column + ':' + owner).getBytes(StandardCharsets.UTF_8);
	}
}
