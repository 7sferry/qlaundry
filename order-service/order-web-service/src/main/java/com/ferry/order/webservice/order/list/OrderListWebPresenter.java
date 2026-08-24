package com.ferry.order.webservice.order.list;

import com.ferry.order.core.order.list.OrderListPresenter;
import com.ferry.order.core.order.list.OrderListResponse;
import com.ferry.order.webservice.order.list.OrderListWebResponse.Item;
import com.ferry.order.webservice.order.list.OrderListWebResponse.Order;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class OrderListWebPresenter implements OrderListPresenter{
	private ResponseEntity<OrderListWebResponse> responseEntity;

	@Override
	public void present(OrderListResponse response){
		List<Order> orders = response.orders().stream()
				.map(o -> {
					List<Item> items = response.itemsByOrderId().getOrDefault(o.id(), List.of()).stream()
							.map(i -> new Item(i.type().name(), i.label(), i.quantity()))
							.toList();
					return new Order(o.id(), o.orderNumberValue(), o.customerId(), o.customerNameValue(),
							o.customerPhoneValue(), o.customerEmailValue(), o.customerAddressValue(), o.serviceId(),
							o.serviceName(), o.unit().name(), o.unitPrice().value(), o.quantity(), o.weightKg(),
							o.subtotal().value(), o.discount().value(), o.totalPrice().value(), o.priority().name(),
							o.paymentMethod().name(), o.paymentStatus().name(), o.status().name(), o.notesValue(),
							o.staffNotesValue(), o.pickupAt().toEpochMilli(),
							o.estimatedDeliveryAt().toEpochMilli(),
							o.completedAt() == null ? null : o.completedAt().toEpochMilli(),
							o.createdAt().toEpochMilli(), items);
				})
				.toList();
		responseEntity = ResponseEntity.ok(new OrderListWebResponse(orders));
	}

}
