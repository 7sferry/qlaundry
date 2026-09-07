package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionFilter;
import com.ferry.promotion.domain.promotion.PromotionListSortBy;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import com.ferry.utils.pagination.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionListUseCase implements PromotionListUseCase{
	private final PromotionListGateway gateway;

	@Override
	public void execute(PromotionListRequest request, PromotionAuthPrincipal principal,
	                    PromotionListPresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		PromotionListSortBy sortBy = request.sortBy() == null ? PromotionListSortBy.ID : request.sortBy();
		SortDirection sortDir = request.sortDir() == null ? SortDirection.DESC : request.sortDir();
		PageDirection direction = request.direction() == null ? PageDirection.NEXT : request.direction();
		PageCursor cursor = request.cursor() == null ? null : CursorCodec.decode(request.cursor());
		PromotionFilter filter = PromotionFilter.builder()
				.tenantId(tenantId.value())
				.code(request.code())
				.name(request.name())
				.type(request.type())
				.activeOnly(request.activeOnly() == null || request.activeOnly())
				.currentOnly(request.currentOnly() != null && request.currentOnly())
				.sortBy(sortBy)
				.sortDir(sortDir)
				.pageDirection(direction)
				.cursor(cursor)
				.build();
		CursorFetch<PromotionDomain> fetch = gateway.findByFilter(filter);
		CursorPage<PromotionDomain> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> switch(sortBy){
					case NAME -> List.of(row.name(), row.id());
					case END_AT -> List.of(String.valueOf(row.endAt().toEpochMilli()), row.id());
					case CODE -> List.of(row.codeValue(), row.id());
					case ID -> List.of(row.id(), row.id());
				});
		presenter.present(new PromotionListResponse(page.items(), page.nextCursor(), page.prevCursor()));
	}

}
