package com.ferry.utils.generator;

import de.huxhorn.sulky.ulid.ULID;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class UlidGenerator implements IdGenerator{
	public static final ULID ULID = new ULID();

	@Override
	public String generateId(){
		return ULID.nextULID();
	}

}
