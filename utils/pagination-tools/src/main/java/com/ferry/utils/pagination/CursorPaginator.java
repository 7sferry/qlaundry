package com.ferry.utils.pagination;

import java.util.List;
import java.util.function.Function;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public final class CursorPaginator{

	private CursorPaginator(){
	}

	public static <T> CursorPage<T> paginate(CursorFetch<T> fetch, PageDirection pageDir,
	                                         boolean cursorWasProvided, Function<T, List<String>> sortValueAndId){
		List<T> rows = fetch.rows();
		boolean hasNext;
		boolean hasPrev;
		if(pageDir == PageDirection.PREV){
			hasPrev = fetch.hasMore();
			hasNext = true;
		}else{
			hasNext = fetch.hasMore();
			hasPrev = cursorWasProvided;
		}

		String nextCursor = null;
		String prevCursor = null;
		if(!rows.isEmpty()){
			if(hasNext){
				List<String> last = sortValueAndId.apply(rows.getLast());
				nextCursor = CursorCodec.encode(last.get(0), last.get(1));
			}
			if(hasPrev){
				List<String> first = sortValueAndId.apply(rows.getFirst());
				prevCursor = CursorCodec.encode(first.get(0), first.get(1));
			}
		}
		return new CursorPage<>(rows, nextCursor, prevCursor);
	}

}
