package com.ferry.analytics.webservice.event.customer;

import com.ferry.analytics.core.event.customer.CustomerEventPresenter;
import com.ferry.analytics.core.event.customer.CustomerEventResponse;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class StreamCustomerEventPresenter implements CustomerEventPresenter{
	private CustomerEventResponse response;

	@Override
	public void present(CustomerEventResponse response){
		this.response = response;
	}
}
