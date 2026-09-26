package com.ferry.order.webservice.customer.totals;

import com.ferry.order.core.customer.totals.CustomerOrderTotalsPresenter;
import com.ferry.order.core.customer.totals.CustomerOrderTotalsResponse;
import com.ferry.order.webservice.customer.totals.CustomerOrderTotalsRestResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class WebCustomerOrderTotalsPresenter implements CustomerOrderTotalsPresenter{
	private ResponseEntity<CustomerOrderTotalsRestResponse> responseEntity;

	@Override
	public void present(CustomerOrderTotalsResponse response){
		List<Item> items = response.totals().stream()
				.map(t -> new Item(t.customerId(), t.totalOrders(), t.totalSpend(), t.lastOrderAt().toEpochMilli()))
				.toList();
		responseEntity = ResponseEntity.ok(new CustomerOrderTotalsRestResponse(items));
	}

}
