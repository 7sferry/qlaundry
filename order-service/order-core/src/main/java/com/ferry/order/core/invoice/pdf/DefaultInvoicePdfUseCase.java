package com.ferry.order.core.invoice.pdf;

import com.ferry.order.core.invoice.link.InvoiceLinkConstant;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.utils.linksigner.LinkSigner;
import com.ferry.utils.linksigner.SignedLinkPayload;
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
	private final LinkSigner linkSigner;

	@Override
	public void execute(InvoicePdfRequest request, InvoicePdfPresenter presenter){
		request.validate();
		SignedLinkPayload payload = verifyToken(request);
		OrderIdDomain orderId = new OrderIdDomain(payload.fields().get(InvoiceLinkConstant.ORDER_ID_FIELD));
		TenantIdDomain tenantId = new TenantIdDomain(payload.fields().get(InvoiceLinkConstant.TENANT_ID_FIELD));
		OrderDomain order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		List<OrderItemDomain> items = gateway.findItemsByOrderId(orderId);
		byte[] pdf = composer.compose(order, items);
		presenter.present(new InvoicePdfResponse(order, pdf));
	}

	private SignedLinkPayload verifyToken(InvoicePdfRequest request){
		return linkSigner.verify(request.token())
				.filter(signedLinkPayload -> signedLinkPayload.fields().containsKey(InvoiceLinkConstant.ORDER_ID_FIELD)
						&& signedLinkPayload.fields().containsKey(InvoiceLinkConstant.TENANT_ID_FIELD))
				.orElseThrow(() -> new NotFoundException("Invoice link is invalid or expired"));
	}

}
