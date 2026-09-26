package com.ferry.order.core.invoice.pdf;

import com.ferry.order.core.invoice.link.InvoiceLinkConstant;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.tenant.TenantId;
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
	private final InvoiceComposer composer;
	private final LinkSigner linkSigner;

	@Override
	public void execute(InvoicePdfRequest request, InvoicePdfPresenter presenter){
		request.validate();
		SignedLinkPayload payload = verifyToken(request);
		OrderId orderId = new OrderId(payload.fields().get(InvoiceLinkConstant.ORDER_ID_FIELD));
		TenantId tenantId = new TenantId(payload.fields().get(InvoiceLinkConstant.TENANT_ID_FIELD));
		Order order = gateway.findById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		List<OrderItem> items = gateway.findItemsByOrderId(orderId);
		List<OrderPromotion> promotions = gateway.findPromotionsByOrderId(orderId);
		byte[] pdf = composer.compose(order, items, promotions);
		presenter.present(new InvoicePdfResponse(order, pdf));
	}

	private SignedLinkPayload verifyToken(InvoicePdfRequest request){
		return linkSigner.verify(request.token())
				.filter(signedLinkPayload -> signedLinkPayload.fields().containsKey(InvoiceLinkConstant.ORDER_ID_FIELD)
						&& signedLinkPayload.fields().containsKey(InvoiceLinkConstant.TENANT_ID_FIELD))
				.orElseThrow(() -> new NotFoundException("Invoice link is invalid or expired"));
	}

}
