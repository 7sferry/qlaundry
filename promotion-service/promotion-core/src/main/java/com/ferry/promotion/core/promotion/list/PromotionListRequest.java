package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.core.tools.PromotionValidation;
import com.ferry.promotion.domain.promotion.PromotionListSortBy;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionListRequest(String code, String name, PromotionType type, Boolean activeOnly,
                                   Boolean currentOnly, String cursor, PageDirection direction,
                                   PromotionListSortBy sortBy, SortDirection sortDir) implements PromotionValidation{
}
