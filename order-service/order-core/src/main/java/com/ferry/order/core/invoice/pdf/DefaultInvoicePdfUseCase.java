package com.ferry.order.core.invoice.pdf;

import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultInvoicePdfUseCase implements InvoicePdfUseCase{
	private final InvoicePdfGateway gateway;
	private final InvoiceHtmlComposer composer;

	@Override
	public void execute(InvoicePdfRequest request, InvoicePdfPresenter presenter){
		request.validate();
		OrderIdDomain orderId = new OrderIdDomain(request.orderId());
		TenantIdDomain tenantId = new TenantIdDomain(request.tenantId());
		OrderDomain order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		List<OrderItemDomain> items = gateway.findItemsByOrderId(orderId);
		byte[] pdf = composer.compose(order, items);
		presenter.present(new InvoicePdfResponse(order, pdf));
	}

}
