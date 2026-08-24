package com.ferry.order.webservice.order.create;

import com.ferry.order.core.order.create.OrderCreatePresenter;
import com.ferry.order.core.order.create.OrderCreateResponse;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.webservice.order.create.OrderCreateWebResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderCreateWebPresenter implements OrderCreatePresenter{
	private ResponseEntity<OrderCreateWebResponse> responseEntity;

	@Override
	public void present(OrderCreateResponse response){
		OrderDomain order = response.order();
		List<Item> items = response.items().stream()
				.map(o -> new Item(o.type().name(), o.label(), o.quantity()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderCreateWebResponse(order.id(), order.orderNumberValue(),
				order.customerId(), order.customerNameValue(), order.customerPhoneValue(),
				order.customerEmailValue(), order.customerAddressValue(), order.serviceId(), order.serviceName(),
				order.unit().name(), order.unitPrice().value(), order.quantity(), order.weightKg(),
				order.subtotal().value(), order.discount().value(), order.totalPrice().value(),
				order.priority().name(), order.paymentMethod().name(), order.paymentStatus().name(),
				order.status().name(), order.notesValue(), order.pickupAt().toEpochMilli(),
				order.estimatedDeliveryAt().toEpochMilli(), order.createdAt().toEpochMilli(), items));
	}

}
