package com.ferry.order.core.order.process;

import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderProcessUseCase implements OrderProcessUseCase{
	private final OrderProcessGateway gateway;

	@Override
	public void execute(OrderProcessRequest request, OrderAuthPrincipal principal, OrderProcessPresenter presenter){
		request.validate();
		OrderIdDomain orderId = new OrderIdDomain(request.orderId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		OrderDomain order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		NoteDomain staffNotes = request.staffNotes() == null || request.staffNotes().isBlank()
				? null : new NoteDomain(request.staffNotes());
		OrderDomain saved = gateway.save(order.changeStatus(OrderStatus.IN_PROGRESS, staffNotes,
				principal.userId()));
		presenter.present(new OrderProcessResponse(saved));
	}

}
