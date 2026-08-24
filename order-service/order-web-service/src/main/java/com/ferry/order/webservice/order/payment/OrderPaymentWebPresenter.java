package com.ferry.order.webservice.order.payment;

import com.ferry.order.core.order.payment.OrderPaymentPresenter;
import com.ferry.order.core.order.payment.OrderPaymentResponse;
import com.ferry.order.domain.order.OrderDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderPaymentWebPresenter implements OrderPaymentPresenter{
	private ResponseEntity<OrderPaymentWebResponse> responseEntity;

	@Override
	public void present(OrderPaymentResponse response){
		OrderDomain order = response.order();
		responseEntity = ResponseEntity.ok(new OrderPaymentWebResponse(order.id(), order.orderNumberValue(),
				order.paymentMethod().name(), order.paymentStatus().name(), order.totalPrice().value(),
				order.updatedAt().toEpochMilli()));
	}

}
