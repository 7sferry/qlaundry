package com.ferry.order.webservice.invoice.pdf;

import com.ferry.order.core.invoice.pdf.InvoicePdfRequest;
import com.ferry.order.core.invoice.pdf.InvoicePdfUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class InvoicePdfWebController{
	private final InvoicePdfUseCase invoicePdfUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/public/invoice/pdf")
	public ResponseEntity<?> downloadPdf(InvoicePdfRequest request){
		InvoicePdfWebPresenter presenter = new InvoicePdfWebPresenter();
		invoicePdfUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
