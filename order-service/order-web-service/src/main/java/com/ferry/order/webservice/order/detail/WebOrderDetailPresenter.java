package com.ferry.order.webservice.order.detail;

import com.ferry.order.core.order.detail.OrderDetailPresenter;
import com.ferry.order.core.order.detail.OrderDetailResponse;
import com.ferry.order.domain.order.Order;
import com.ferry.order.webservice.order.detail.OrderDetailRestResponse.Item;
import com.ferry.order.webservice.order.detail.OrderDetailRestResponse.Promotion;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebOrderDetailPresenter implements OrderDetailPresenter{
	private ResponseEntity<OrderDetailRestResponse> responseEntity;

	@Override
	public void present(OrderDetailResponse response){
		Order order = response.order();
		List<Promotion> promotions = response.promotions().stream()
				.map(o -> new Promotion(o.promotionId(), o.code(), o.discountAmount().value()))
				.toList();
		List<Item> items = response.items().stream()
				.map(o -> new Item(o.type().name(), o.label(), o.quantity()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderDetailRestResponse(order.id(), order.orderNumberValue(),
				order.customerId(), order.customerNameValue(), order.customerPhoneValue(),
				order.customerEmailValue(), order.customerAddressValue(), order.serviceId(), order.serviceName(),
				order.unit().name(), order.unitPrice().value(), order.quantity(), order.weightKg(),
				order.subtotal().value(), order.discount().value(), promotions, order.totalPrice().value(),
				order.priority().name(), order.paymentMethod().name(), order.paymentStatus().name(),
				order.status().name(), order.notesValue(), order.staffNotesValue(),
				order.pickupAt().toEpochMilli(), order.estimatedDeliveryAt().toEpochMilli(),
				order.completedAt() == null ? null : order.completedAt().toEpochMilli(),
				order.createdAt().toEpochMilli(), items));
	}

}
