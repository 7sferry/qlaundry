package com.ferry.utils.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CursorFetch<T>(List<T> rows, boolean hasMore){

	public static <T> CursorFetch<T> of(List<T> rawRows, int pageSize, PageDirection direction){
		boolean hasMore = rawRows.size() > pageSize;
		List<T> trimmed = hasMore ? rawRows.subList(0, pageSize) : rawRows;
		List<T> displayOrder = direction == PageDirection.PREV ? reversed(trimmed) : trimmed;
		return new CursorFetch<>(displayOrder, hasMore);
	}

	private static <T> List<T> reversed(List<T> list){
		List<T> copy = new ArrayList<>(list);
		Collections.reverse(copy);
		return copy;
	}

}
