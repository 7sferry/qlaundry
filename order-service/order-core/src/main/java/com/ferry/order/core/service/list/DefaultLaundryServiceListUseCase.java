package com.ferry.order.core.service.list;

import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
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
		LaundryServiceFilter filter = LaundryServiceFilter.builder()
				.tenantId(tenantId.value())
				.name(request.name())
				.category(request.category())
				.activeOnly(request.activeOnly() == null || request.activeOnly())
				.build();
		List<LaundryServiceDomain> services = gateway.findByFilter(filter);
		presenter.present(new LaundryServiceListResponse(services));
	}

}
