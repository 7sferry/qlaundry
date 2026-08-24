package com.ferry.utils.pagination;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CursorPage<T>(List<T> items, String nextCursor, String prevCursor){
}
