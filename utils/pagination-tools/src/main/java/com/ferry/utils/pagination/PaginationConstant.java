package com.ferry.utils.pagination;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public final class PaginationConstant{
	public static final int DEFAULT_PAGE_SIZE = 3;
	public static final int MAX_PAGE_SIZE = 50;

	private PaginationConstant(){
	}

	public static int resolvePageSize(Integer requested){
		if(requested == null || requested < 1){
			return DEFAULT_PAGE_SIZE;
		}
		return Math.min(requested, MAX_PAGE_SIZE);
	}
}
