package com.ferry.order.core.invoice.link;

import com.ferry.order.domain.token.OrderAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface InvoiceLinkUseCase{
	void execute(InvoiceLinkRequest request, OrderAuthPrincipal principal, InvoiceLinkPresenter presenter);
}
