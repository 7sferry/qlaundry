package com.ferry.order.core.service.list;

import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.domain.service.ServiceListSortBy;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.pagination.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultLaundryServiceListUseCase implements LaundryServiceListUseCase{
	private final LaundryServiceListGateway gateway;

	@Override
	public void execute(LaundryServiceListRequest request, OrderAuthPrincipal principal,
	                    LaundryServiceListPresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		ServiceListSortBy sortBy = request.sortBy() == null ? ServiceListSortBy.ID : request.sortBy();
		SortDirection sortDir = request.sortDir() == null ? SortDirection.DESC : request.sortDir();
		PageDirection direction = request.direction() == null ? PageDirection.NEXT : request.direction();
		PageCursor cursor = request.cursor() == null ? null : CursorCodec.decode(request.cursor());
		LaundryServiceFilter filter = LaundryServiceFilter.builder()
				.tenantId(tenantId.value())
				.name(request.name())
				.category(request.category())
				.activeOnly(request.activeOnly() == null || request.activeOnly())
				.sortBy(sortBy)
				.sortDir(sortDir)
				.pageDirection(direction)
				.cursor(cursor)
				.build();
		CursorFetch<LaundryServiceDomain> fetch = gateway.findByFilter(filter);
		CursorPage<LaundryServiceDomain> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> List.of(sortBy == ServiceListSortBy.NAME ? row.name() : row.id(), row.id()));
		presenter.present(new LaundryServiceListResponse(page.items(), page.nextCursor(), page.prevCursor()));
	}

}
