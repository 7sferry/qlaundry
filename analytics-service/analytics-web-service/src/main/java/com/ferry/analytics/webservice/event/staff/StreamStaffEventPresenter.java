package com.ferry.analytics.webservice.event.staff;

import com.ferry.analytics.core.event.staff.StaffEventPresenter;
import com.ferry.analytics.core.event.staff.StaffEventResponse;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class StreamStaffEventPresenter implements StaffEventPresenter{
	private StaffEventResponse response;

	@Override
	public void present(StaffEventResponse response){
		this.response = response;
	}
}
