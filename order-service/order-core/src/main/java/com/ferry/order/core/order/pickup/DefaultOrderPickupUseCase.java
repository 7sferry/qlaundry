package com.ferry.order.core.order.pickup;

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
public class DefaultOrderPickupUseCase implements OrderPickupUseCase{
	private final OrderPickupGateway gateway;

	@Override
	public void execute(OrderPickupRequest request, OrderAuthPrincipal principal, OrderPickupPresenter presenter){
		request.validate();
		OrderIdDomain orderId = new OrderIdDomain(request.orderId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		OrderDomain order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		NoteDomain staffNotes = request.staffNotes() == null || request.staffNotes().isBlank()
				? null : new NoteDomain(request.staffNotes());
		OrderDomain saved = gateway.save(order.changeStatus(OrderStatus.PICKED_UP, staffNotes,
				principal.userId()));
		presenter.present(new OrderPickupResponse(saved));
	}

}
