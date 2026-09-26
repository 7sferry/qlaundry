package com.ferry.order.webservice.invoice.link;

import com.ferry.order.core.invoice.link.InvoiceLinkPresenter;
import com.ferry.order.core.invoice.link.InvoiceLinkResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebInvoiceLinkPresenter implements InvoiceLinkPresenter{
	private ResponseEntity<InvoiceLinkRestResponse> responseEntity;

	@Override
	public void present(InvoiceLinkResponse response){
		responseEntity = ResponseEntity.ok(new InvoiceLinkRestResponse(response.token(), response.expiresAt()));
	}

}
