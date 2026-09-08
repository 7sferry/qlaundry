package com.ferry.utils.pagination;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public enum PageDirection{
	NEXT, PREV,
	;

	public static PageDirection direction(String before){
		return before != null ? PageDirection.PREV : PageDirection.NEXT;
	}

}
