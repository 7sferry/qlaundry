package com.ferry.order.webservice.invoice.pdf;

import com.ferry.order.core.invoice.pdf.InvoicePdfPresenter;
import com.ferry.order.core.invoice.pdf.InvoicePdfResponse;
import lombok.Getter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class InvoicePdfWebPresenter implements InvoicePdfPresenter{
	private ResponseEntity<byte[]> responseEntity;

	@Override
	public void present(InvoicePdfResponse response){
		String filename = response.order().orderNumberValue() + ".pdf";
		ContentDisposition contentDisposition = ContentDisposition.inline().filename(filename).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		responseEntity = ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(response.pdf());
	}

}
