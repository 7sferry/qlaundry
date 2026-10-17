package com.ferry.order.webservice.order.assignstaffs;

import com.ferry.order.core.order.assignstaffs.OrderAssignStaffsPresenter;
import com.ferry.order.core.order.assignstaffs.OrderAssignStaffsResponse;
import com.ferry.order.domain.order.Order;
import com.ferry.order.webservice.order.assignstaffs.OrderAssignStaffsRestResponse.Staff;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebOrderAssignStaffsPresenter implements OrderAssignStaffsPresenter{
	private ResponseEntity<OrderAssignStaffsRestResponse> responseEntity;

	@Override
	public void present(OrderAssignStaffsResponse response){
		Order order = response.order();
		List<Staff> staffs = response.staffs().stream()
				.map(o -> new Staff(o.staffId()))
				.toList();
		responseEntity = ResponseEntity.ok(new OrderAssignStaffsRestResponse(order.id(), order.orderNumberValue(),
				order.status().name(), staffs));
	}

}
