package com.ferry.analytics.webservice.event.laundryservice;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventPresenter;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventResponse;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class LaundryServiceEventStreamPresenter implements LaundryServiceEventPresenter{
	private LaundryServiceEventResponse response;

	@Override
	public void present(LaundryServiceEventResponse response){
		this.response = response;
	}
}
