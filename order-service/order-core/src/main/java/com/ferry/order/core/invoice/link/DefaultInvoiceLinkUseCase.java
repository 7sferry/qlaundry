package com.ferry.order.core.invoice.link;

import com.ferry.order.core.invoice.pdf.InvoicePdfGateway;
import com.ferry.order.core.tools.InvoiceLinkSigner;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultInvoiceLinkUseCase implements InvoiceLinkUseCase{
	private final InvoicePdfGateway gateway;
	private final InvoiceLinkSigner signer;

	@Override
	public void execute(InvoiceLinkRequest request, OrderAuthPrincipal principal, InvoiceLinkPresenter presenter){
		request.validate();
		OrderIdDomain orderId = new OrderIdDomain(request.orderId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		gateway.findById(orderId, tenantId).orElseThrow(() -> new NotFoundException("Order Not Found"));
		long expiresAt = Instant.now().plus(InvoiceLinkConstant.LINK_TTL).toEpochMilli();
		String token = signer.sign(orderId.value(), tenantId.value(), expiresAt);
		presenter.present(new InvoiceLinkResponse(token, expiresAt));
	}

}
