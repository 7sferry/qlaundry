package com.ferry.promotion.domain.promotion;

import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import lombok.Builder;

import java.time.Instant;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record PromotionFilter(String tenantId, String code, String name, PromotionType type, boolean activeOnly,
                              boolean currentOnly, PromotionListSortBy sortBy, SortDirection sortDir,
                              PageDirection pageDirection, PageCursor cursor){

	public Instant cursorEndAt(){
		if(cursor == null){
			return null;
		}
		return Instant.ofEpochMilli(Long.parseLong(cursor.sortValue()));
	}

	public String codeStartsWith(){
		if(code == null || code.isBlank()){
			return null;
		}
		return code.trim().toUpperCase(Locale.ROOT) + '%';
	}

	public String nameStartsWith(){
		if(name == null || name.isBlank()){
			return null;
		}
		return name.toLowerCase() + '%';
	}

	public Short typeValue(){
		return type == null ? null : type.getValue();
	}

}
