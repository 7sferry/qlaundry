package com.ferry.order.webservice.order.detail;

import com.ferry.order.core.order.detail.OrderDetailPresenter;
import com.ferry.order.core.order.detail.OrderDetailResponse;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.webservice.order.detail.OrderDetailWebResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderDetailWebPresenter implements OrderDetailPresenter{
	private ResponseEntity<OrderDetailWebResponse> responseEntity;

	@Override
	public void present(OrderDetailResponse response){
		OrderDomain order = response.order();
		List<Item> items = response.items().stream()
				.map(o -> new Item(o.type().name(), o.label(), o.quantity()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderDetailWebResponse(order.id(), order.orderNumberValue(),
				order.customerId(), order.customerNameValue(), order.customerPhoneValue(),
				order.customerEmailValue(), order.customerAddressValue(), order.serviceId(), order.serviceName(),
				order.unit().name(), order.unitPrice().value(), order.quantity(), order.weightKg(),
				order.subtotal().value(), order.discount().value(), order.totalPrice().value(),
				order.priority().name(), order.paymentMethod().name(), order.paymentStatus().name(),
				order.status().name(), order.notesValue(), order.staffNotesValue(),
				order.pickupAt().toEpochMilli(), order.estimatedDeliveryAt().toEpochMilli(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.createdAt().toEpochMilli(), items));
	}

}
