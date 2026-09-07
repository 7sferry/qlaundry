package com.ferry.order.webservice.customer.totals;

import com.ferry.order.core.customer.totals.CustomerOrderTotalsPresenter;
import com.ferry.order.core.customer.totals.CustomerOrderTotalsResponse;
import com.ferry.order.webservice.customer.totals.CustomerOrderTotalsWebResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class CustomerOrderTotalsWebPresenter implements CustomerOrderTotalsPresenter{
	private ResponseEntity<CustomerOrderTotalsWebResponse> responseEntity;

	@Override
	public void present(CustomerOrderTotalsResponse response){
		List<Item> items = response.totals().stream()
				.map(t -> new Item(t.customerId(), t.totalOrders(), t.totalSpend(), t.lastOrderAt().toEpochMilli()))
				.toList();
		responseEntity = ResponseEntity.ok(new CustomerOrderTotalsWebResponse(items));
	}

}
