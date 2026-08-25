package com.ferry.order.webservice.invoice.link;

import com.ferry.order.core.invoice.link.InvoiceLinkRequest;
import com.ferry.order.core.invoice.link.InvoiceLinkUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class InvoiceLinkWebController{
	private final InvoiceLinkUseCase invoiceLinkUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/invoice/link")
	public ResponseEntity<?> getLink(InvoiceLinkRequest request,
	                                 @AuthenticationPrincipal OrderAuthPrincipal principal){
		InvoiceLinkWebPresenter presenter = new InvoiceLinkWebPresenter();
		invoiceLinkUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
